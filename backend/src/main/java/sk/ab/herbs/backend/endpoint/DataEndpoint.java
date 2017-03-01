package sk.ab.herbs.backend.endpoint;

import com.google.api.server.spi.config.Api;
import com.google.api.server.spi.config.ApiMethod;
import com.google.api.server.spi.config.ApiNamespace;
import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.FetchOptions;
import com.google.appengine.api.datastore.KeyFactory;
import com.google.appengine.api.datastore.Query;
import com.google.appengine.repackaged.com.google.api.client.util.Strings;
import com.google.appengine.repackaged.com.google.gson.JsonElement;
import com.google.appengine.repackaged.com.google.gson.JsonObject;
import com.google.appengine.repackaged.com.google.gson.JsonParser;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import javax.inject.Named;

import sk.ab.common.Constants;


/** An endpoint class for data access */
@Api(
        name = "dataApi",
        version = "v1",
        namespace = @ApiNamespace(
                ownerDomain = "backend.herbs.ab.sk",
                ownerName = "backend.herbs.ab.sk",
                packagePath="endpoint"
        )
)
public class DataEndpoint {

    @ApiMethod(
            name = "plant",
            path = "plant/{taxonomyName}",
            httpMethod = ApiMethod.HttpMethod.POST)
    public Entity plant(@Named("taxonomyName") String taxonomyName,
                       @Named("taxonomyWiki") String taxonomyWiki) {

        DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();

        if (taxonomyWiki == null) {
            taxonomyWiki = taxonomyName;
        }
        String[] hlp = taxonomyName.split(" ");
        String genus = hlp[0];

        Entity plantEntity = new Entity("Plant", taxonomyName);
        plantEntity.setProperty("label_la", taxonomyName);

        Query.Filter propertyFilter =
                new Query.FilterPredicate(Constants.LANGUAGE_LA, Query.FilterOperator.EQUAL, genus);
        Query q = new Query("Genus").setFilter(propertyFilter);

        List<Entity> genuses =
                datastore.prepare(q).asList(FetchOptions.Builder.withDefaults());
        if (genuses.size() == 1) {
            Entity entity = genuses.get(0);

            plantEntity.setProperty("taxonomyKey", entity.getKey());
        }

        plantEntity.setProperty("wikidata", getWikidata(taxonomyWiki));

        getNamesFromWikiSpecies(plantEntity, taxonomyWiki);

        modifyEntityAddLinks(plantEntity);

        datastore.put(plantEntity);

        return plantEntity;
    }

    @ApiMethod(
            name = "insert",
            path = "{taxonomyName}",
            httpMethod = ApiMethod.HttpMethod.POST)
    public Entity insert(@Named("taxonomyName") String taxonomyName,
                         @Named("taxonomyPath") String taxonomyPath,
                         @Named("parentPath") String parentPath,
                         @Named("name") String name,
                         @Named("wikiName") String wikiName) {

        DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();

        String[] path = taxonomyPath.split(",");
        String[] parent = parentPath.split(",");
        KeyFactory.Builder builder = new KeyFactory.Builder(path[0], parent[0]);
        if (path.length > 1) {
            for(int i=1; i < path.length; i++) {
                builder.addChild(path[i], parent[i]);
            }
        }

        Entity taxonomyEntity = new Entity(taxonomyName, name, builder.getKey());
        if (wikiName == null) {
            wikiName = name;
        }
        modifyEntityWikiSpecies(taxonomyEntity, wikiName);
        datastore.put(taxonomyEntity);

        return taxonomyEntity;
    }

    private String getWikidata(String name) {
        try {
            Document doc = Jsoup.connect("https://species.wikimedia.org/wiki/" + name).get();

            String wikiPage = doc.getElementsByAttributeValue("title", "Edit interlanguage links").attr("href");

            return wikiPage.substring(wikiPage.lastIndexOf("/")+1, wikiPage.indexOf("#"));
        } catch (IOException e) {
            e.printStackTrace();
        }

        return null;
    }

    private void getNamesFromWikiSpecies(Entity entity, String name) {
        try {
            Document doc = Jsoup.connect("https://species.wikimedia.org/w/index.php?title=" + name + "&action=edit").get();

            String wikiPage = doc.getElementsByTag("textarea").val();
            String vn = wikiPage.substring(wikiPage.indexOf("{{VN"), wikiPage.indexOf("}}", wikiPage.indexOf("{{VN"))).replace("\n", "");
            String[] vnItems = vn.substring(5, vn.length()).split("\\|");
            for(String vnItem : vnItems) {
                String[] hlp = vnItem.split("=");
                if(hlp.length > 1) {
                    String language = hlp[0];

                    String[] names = hlp[1].trim().split(", ");
                    if (names.length == 1) {
                        names = hlp[1].split(" / ");
                    }
                    List<String> speciesValuesOld = new ArrayList<>(Arrays.asList(names));
                    List<String> speciesValues = new ArrayList<>();
                    for(String speciesValue : speciesValuesOld) {
                        speciesValues.add(speciesValue.trim());
                    }
                    if (speciesValues.size() > 1) {
                        entity.setProperty("label_"+language, speciesValues.get(0));
                        speciesValues.remove(0);
                        if (speciesValues.size() > 0) {
                            entity.setProperty("alias_"+language, speciesValues);
                        }
                    } else {
                        entity.setProperty("label_"+language, hlp[1].trim());
                    }
                }
            }

            String[] lines = wikiPage.split("\n");

            Set<String> synonymSet = new TreeSet<>();
            String key = "synonym";
            boolean isSynonyms = false;
            for(String line : lines) {
                if (line.contains("Synonym") || line.contains("{{SN")) {
                    isSynonyms = true;
                }

                if (!isSynonyms) {
                    continue;
                }

                if (line.trim().length() > 0) {
                    if (line.contains("References") || line.contains("Vernacular names")
                            || (line.contains("Hybrids") && line.contains("=="))
                            || (line.contains("Notes") && line.contains("=="))) {
                        break;
                    }

                    if (line.contains("''") && line.substring(line.indexOf("''")+2).contains("''")) {
                        String synonym = line.substring(line.indexOf("''")+2, line.indexOf("''", line.indexOf("''")+2));

                        if (synonym.startsWith("[[")) {
                            synonym = synonym.substring(2);
                        }
                        if (synonym.endsWith("]]")) {
                            synonym = synonym.substring(0,synonym.length()-2);
                        }

                        synonymSet.add(synonym.replace("'", ""));
                    }
                }
            }
            synonymSet.remove(name);
            synonymSet.remove("{{BASEPAGENAME}}");
            synonymSet.remove("Homotypic");
            synonymSet.remove("Heterotypic");
            synonymSet.remove("Basionym");
            synonymSet.remove("Homonyms");
            synonymSet.remove("vide");
            List<String> synonymList = new ArrayList<>();
            for(String synonym : synonymSet) {
                synonymList.add(synonym);
            }
            if (synonymList.size() > 0) {
                entity.setProperty(key, synonymList);
            }

        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    private void modifyEntityAddLinks(Entity entity) {
        try {
            String id = entity.getProperty("wikidata").toString();

            URL url = new URL("https://www.wikidata.org/wiki/Special:EntityData/" + id + ".json");
            HttpURLConnection request = (HttpURLConnection) url.openConnection();
            request.connect();

            JsonParser jp = new JsonParser();
            JsonElement root = jp.parse(new InputStreamReader((InputStream) request.getContent()));
            JsonObject wikidata = root.getAsJsonObject().getAsJsonObject("entities").getAsJsonObject(id);

            JsonObject sitelinks = wikidata.getAsJsonObject("sitelinks");

            for (Map.Entry<String,JsonElement> entry : sitelinks.entrySet()) {
                JsonObject value = entry.getValue().getAsJsonObject();

                String site = value.get("site").getAsString();
                site = site.substring(0, site.length()-4);

                entity.setProperty("wiki_"+site, value.get("url").getAsString());
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void modifyEntityWikiSpecies(Entity entity, String name) {
        try {
            Document doc = Jsoup.connect("https://species.wikimedia.org/w/index.php?title=" + name + "&action=edit").get();

            String wikiPage = doc.getElementsByTag("textarea").val();
            if (wikiPage.contains("{{VN")) {
                String vn = wikiPage.substring(wikiPage.indexOf("{{VN"), wikiPage.indexOf("}}", wikiPage.indexOf("{{VN"))).replace("\n", "");
                String[] languages = vn.substring(5, vn.length()).split("\\|");
                for (String language : languages) {
                    String[] hlp = language.split("=");
                    if (hlp.length > 1) {
                        String[] multiValue = hlp[1].trim().split(", ");
                        if (multiValue.length > 1) {
                            entity.setProperty(hlp[0].trim(), Arrays.asList(multiValue));
                        } else {
                            multiValue = hlp[1].split(" / ");
                            if (multiValue.length > 1) {
                                entity.setProperty(hlp[0].trim(), Arrays.asList(multiValue));
                            } else {
                                entity.setProperty(hlp[0].trim(), hlp[1].trim());
                            }
                        }
                    }
                }
            }
            entity.setProperty("la", name);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
