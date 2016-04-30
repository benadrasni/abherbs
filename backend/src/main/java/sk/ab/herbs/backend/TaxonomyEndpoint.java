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
        name = "insertDomain",
        path = "{name}",
        httpMethod = ApiMethod.HttpMethod.POST)
    public Entity insertDomain(@Named("name") String name) {
        DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();

        Entity domain = new Entity("Superregnum", name);
        modifyEntity(domain, name);
        datastore.put(domain);

        return domain;
    }

    @ApiMethod(
            name = "insert",
            path = "{taxonomyParent}/{taxonomyName}",
            httpMethod = ApiMethod.HttpMethod.POST)
    public Entity insert(@Named("taxonomyParent") String taxonomyParent, @Named("taxonomyName") String taxonomyName,
                         @Named("parent") String parent, @Named("name") String name) {
        DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
        Key parentKey = KeyFactory.createKey(taxonomyParent, parent);

        Entity taxonomyEntity = new Entity(taxonomyName, name, parentKey);
        modifyEntity(taxonomyEntity, name);
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
                    String[] multiValue = hlp[1].split(", ");
                    if (multiValue.length > 1) {
                        entity.setProperty(hlp[0], Arrays.asList(multiValue));
                    } else {
                        multiValue = hlp[1].split(" / ");
                        if (multiValue.length > 1) {
                            entity.setProperty(hlp[0], Arrays.asList(multiValue));
                        } else {
                            entity.setProperty(hlp[0], hlp[1]);
                        }
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
}
