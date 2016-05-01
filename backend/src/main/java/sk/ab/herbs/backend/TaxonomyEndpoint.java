package sk.ab.herbs.backend;

import com.google.api.server.spi.config.Api;
import com.google.api.server.spi.config.ApiMethod;
import com.google.api.server.spi.config.ApiNamespace;
import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.datastore.KeyFactory;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.io.IOException;
import java.util.Arrays;

import javax.inject.Named;

/** An endpoint class we are exposing */
@Api(
        name = "wikispeciesApi",
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
                         @Named("parentPath") String parentPath, @Named("name") String name) {
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
        modifyEntity(taxonomyEntity, name);
        datastore.put(taxonomyEntity);

        return taxonomyEntity;
    }

    @ApiMethod(
            name = "insertAngiosperms",
            path = "Angiosperms",
            httpMethod = ApiMethod.HttpMethod.POST)
    public Entity insertAngiosperms() {
        DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
        Key parentKey = new KeyFactory.Builder("Superregnum", "Eukaryota")
                .addChild("Regnum", "Plantae").getKey();

        Entity taxonomyEntity = new Entity("Cladus", "Angiosperms", parentKey);
        modifyEntity(taxonomyEntity, "Magnoliopsida");
        datastore.put(taxonomyEntity);

        return taxonomyEntity;
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
                        entity.setProperty(hlp[0], Arrays.asList(multiValue));
                    } else {
                        multiValue = hlp[1].split(" / ");
                        if (multiValue.length > 1) {
                            entity.setProperty(hlp[0], Arrays.asList(multiValue));
                        } else {
                            entity.setProperty(hlp[0], hlp[1].trim());
                        }
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
}
