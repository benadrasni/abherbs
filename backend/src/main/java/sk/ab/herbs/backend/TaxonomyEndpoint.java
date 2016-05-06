package sk.ab.herbs.backend;

import com.google.api.server.spi.config.Api;
import com.google.api.server.spi.config.ApiMethod;
import com.google.api.server.spi.config.ApiNamespace;
import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.EntityNotFoundException;
import com.google.appengine.api.datastore.FetchOptions;
import com.google.appengine.api.datastore.KeyFactory;
import com.google.appengine.api.datastore.Query;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.inject.Named;

/** An endpoint class we are exposing */
@Api(
        name = "taxonomyApi",
        version = "v1",
        namespace = @ApiNamespace(
                ownerDomain = "backend.herbs.ab.sk",
                ownerName = "backend.herbs.ab.sk",
                packagePath=""
        )
)
public class TaxonomyEndpoint {

    @ApiMethod(
            name = "insert",
            path = "{taxonomyName}",
            httpMethod = ApiMethod.HttpMethod.POST)
    public Entity insert(@Named("taxonomyName") String taxonomyName, @Named("taxonomyPath") String taxonomyPath,
                         @Named("parentPath") String parentPath, @Named("name") String name, @Named("wikiName") String wikiName) {
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
        modifyEntity(taxonomyEntity, wikiName);
        datastore.put(taxonomyEntity);

        return taxonomyEntity;
    }

    @ApiMethod(
            name = "getTaxonomyByFamily",
            path = "find/{taxonName}/{taxonValue}",
            httpMethod = ApiMethod.HttpMethod.GET)
    public List<Taxon> getTaxonomyByFamily(@Named("taxonName") String taxonName, @Named("taxonValue") String taxonValue,
                                           @Named("lang") String language) {
        DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();

        Query.Filter propertyFilter =
                new Query.FilterPredicate("la", Query.FilterOperator.EQUAL, taxonValue);
        Query q = new Query(taxonName).setFilter(propertyFilter);

        List<Taxon> results = new ArrayList<>();

        List<Entity> families =
                datastore.prepare(q).asList(FetchOptions.Builder.withDefaults());
        if (families.size() == 1) {
            Entity entity = families.get(0);

            do {
                Taxon taxon = new Taxon();
                taxon.setType(entity.getKind());

                Object latinProperty = entity.getProperty("la");
                List<String> latinName = new ArrayList<>();
                if (latinProperty != null) {
                    if (latinProperty instanceof String) {
                        latinName.add((String)latinProperty);
                    } else if (latinProperty instanceof List) {
                        latinName.addAll((List<String>)latinProperty);
                    }
                }
                taxon.setLatinName(latinName);

                List<String> name = new ArrayList<>();
                Object property = entity.getProperty(language);
                if (property != null) {
                  if (property instanceof String) {
                      name.add((String)property);
                  } else if (property instanceof List) {
                      name.addAll((List<String>)property);
                  }
                }
                taxon.setName(name);

                results.add(taxon);

                if (entity.getParent() != null) {
                    try {
                        entity = datastore.get(entity.getParent());
                    } catch (EntityNotFoundException e) {
                        entity = null;
                    }
                } else {
                    entity = null;
                }
            }
            while (entity != null);
        }

        return results;
    }

    private void modifyEntity(Entity entity, String name) {
        try {
            Document doc = Jsoup.connect("https://species.wikimedia.org/w/index.php?title=" + name + "&action=edit").get();

            String wikiPage = doc.getElementsByTag("textarea").val();
            String vn = wikiPage.substring(wikiPage.indexOf("{{VN"), wikiPage.indexOf("}}", wikiPage.indexOf("{{VN"))).replace("\n", "");
            String[] languages = vn.substring(5, vn.length()-2).split("\\|");
            for(String language : languages) {
                String[] hlp = language.split("=");
                if(hlp.length > 1) {
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
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
}
