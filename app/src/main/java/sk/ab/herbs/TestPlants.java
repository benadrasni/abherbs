package sk.ab.herbs;

import java.util.*;

/**
 * Created with IntelliJ IDEA.
 * User: adrian
 * Date: 26.2.2013
 * Time: 18:04
 * To change this template use File | Settings | File Templates.
 */
public class TestPlants {
  private static List<PlantHeader> plantHeaders = new ArrayList<PlantHeader>();
  private static Map<Integer, Plant> plants = new HashMap<Integer, Plant>();

  static {
    plantHeaders.add(new PlantHeader(1, "pastierska kapsička", "http://commondatastorage.googleapis.com/abplants/CapselleBursaPastoris/cbp1.png", "kapustovité", 3));
    plantHeaders.add(new PlantHeader(2, "peniažtek roľný", "http://commondatastorage.googleapis.com/abplants/Thlaspi_arvense.png", "kapustovité", 3));
    plantHeaders.add(new PlantHeader(3, "tedzálka piesočná", "http://commondatastorage.googleapis.com/abplants/Teesdalia_nudicaulis.png", "kapustovité", 3));
    plantHeaders.add(new PlantHeader(4, "jarmilka jarná", "http://commondatastorage.googleapis.com/abplants/Erophila_verna.png", "kapustovité", 3));
    plantHeaders.add(new PlantHeader(5, "plesnivec alpínsky", "http://commondatastorage.googleapis.com/abplants/Leontopodium_alpinum.png", "astrovité", 1));
    plantHeaders.add(new PlantHeader(6, "machovička položená", "http://commondatastorage.googleapis.com/abplants/Sagina_procumbens.png", "silenkovité", 2));
    plantHeaders.add(new PlantHeader(7, "peniažtek prerastenolistý"));
    plantHeaders.add(new PlantHeader(8, "žerucha poľná"));
    plantHeaders.add(new PlantHeader(9, "vesnovka obyčajná"));
    plantHeaders.add(new PlantHeader(10, "cesnačka lekárska"));
    plantHeaders.add(new PlantHeader(11, "arábka chlpatá"));
    plantHeaders.add(new PlantHeader(12, "lipkavec mäkký"));
    plantHeaders.add(new PlantHeader(13, "lipkavec obyčajný"));
    plantHeaders.add(new PlantHeader(14, "reďkev ohnicová"));
    plantHeaders.add(new PlantHeader(15, "skorocel prostredný"));
    plantHeaders.add(new PlantHeader(16, "skorocel kopijovitý"));
    plantHeaders.add(new PlantHeader(17, "jahoda trávnicová"));
    plantHeaders.add(new PlantHeader(18, "hviezdica prostredná"));
    plantHeaders.add(new PlantHeader(19, "hviezdica trávovitá"));
    plantHeaders.add(new PlantHeader(20, "burinka okolíkatá"));
    plantHeaders.add(new PlantHeader(21, "rožec roľný"));
    plantHeaders.add(new PlantHeader(22, "rožec prameniskový"));
    plantHeaders.add(new PlantHeader(23, "rožec päťtyčinkový"));
    plantHeaders.add(new PlantHeader(24, "piesočnica dúškolistá"));
    plantHeaders.add(new PlantHeader(25, "kolenec roľný"));
    plantHeaders.add(new PlantHeader(26, "mydlica lekárska"));
    plantHeaders.add(new PlantHeader(27, "knôtovka biela"));
    plantHeaders.add(new PlantHeader(28, "silenka obyčajná"));
    plantHeaders.add(new PlantHeader(29, "stavikrv vtáčí"));
    plantHeaders.add(new PlantHeader(30, "horčiak štiavolistý"));
    plantHeaders.add(new PlantHeader(31, "kamienkovec roľný"));
    plantHeaders.add(new PlantHeader(32, "rozchodník biely"));
    plantHeaders.add(new PlantHeader(33, "kotúč poľný"));
    plantHeaders.add(new PlantHeader(34, "bolehlav škvrnitý"));
    plantHeaders.add(new PlantHeader(35, "krkoška mámivá"));
    plantHeaders.add(new PlantHeader(36, "bedrovník väčší"));
    plantHeaders.add(new PlantHeader(37, "rasca lúčna"));
    plantHeaders.add(new PlantHeader(38, "tetucha kozia"));
    plantHeaders.add(new PlantHeader(39, "mrkva obyčajná"));
    plantHeaders.add(new PlantHeader(40, "angelika lesná"));

    Plant plant = new Plant(1);
    plant.setTitle("pastierska kapsička");
    plant.setBack_url("http://commondatastorage.googleapis.com/abplants/Capsella_bursa-pastoris.webp");

    plant.setFlower("Kvety sú usporiadané v riedkom strapci s vrcholovým paokolíkom. Štyri biele okvetné lístky.");
    plant.setFruit("Plody sú trojuholníkovité až klinovito srdcovité.");
    plant.setStem("Byľ je bohato rozkonárená.");
    plant.setLeaf("Listy v prízemnej ružici sú gravicovité až perovito dielne. Listy vo vrcholovej časti byle sú celistvookrajové.");
    plant.setHabitat("Vyskytuje sa v burinových porastoch na poliach, vo vinohradoch a v záhradách, na železničných násypoch, rumoviskách a popri cestách.");

    plant.setInflorescence("paokolík");
    plant.setFlower_color("white");
    plant.setNumber_of_petals("4");
    plant.setLeaf_shape("gracovitá");
    plant.setLeaf_margin("celistvookrajový");

    plant.setSpecies("pastierska kapsička");
    plant.setSpecies_latin("Capsella bursa-pastoris");
    plant.setGenus("pastierska");
    plant.setGenus_latin("Capsella");
    plant.setFamily("kapustovité");
    plant.setFamily_latin("Brassicaceae");
    plant.setOrder("magnólie");
    plant.setOrder_latin("Magnoliopsida");
    plant.setCls("magnóliorasty");
    plant.setCls_latin("Magnoliophyta");
    plant.setPhylum("krytosemenné");
    plant.setPhylum_latin("Angiospermae");
    plant.setBranch("semenné");
    plant.setBranch_latin("Spermatophyta");
    plant.setLine("cievnaté");
    plant.setLine_latin("Tracheophyta");
    plant.setSubkingdom("zelené");
    plant.setSubkingdom_latin("Chlorobionta");
    plant.setKingdom("rastliny");
    plant.setKingdom_latin("Plantae");
    plant.setDomain("eukaryoty");
    plant.setDomain_latin("Eukaryota");

    List<String> photo_urls = new ArrayList<String>();
    photo_urls.add("https://commondatastorage.googleapis.com/abplants/CapselleBursaPastoris/cbp1.png");
    photo_urls.add("https://commondatastorage.googleapis.com/abplants/CapselleBursaPastoris/cbp2.png");
    photo_urls.add("https://commondatastorage.googleapis.com/abplants/CapselleBursaPastoris/cbp3.png");
    plant.setPhoto_urls(photo_urls);

    plants.put(1, plant);
  }

  public static List<PlantHeader> getInitial() {
    return new ArrayList<PlantHeader>(plantHeaders);
  }

  public static List<PlantHeader> getPlants(int prevCount) {
    List<PlantHeader> plantHeaders = new ArrayList<PlantHeader>();
    Random r = new Random();
    int count = r.nextInt(prevCount);
    if (count == 0) count++;
    int i = 0;
    for (PlantHeader plantHeader : TestPlants.plantHeaders) {
      if (i == count) break;
      plantHeaders.add(plantHeader);
      i++;
    }

    return plantHeaders;
  }

  public static Plant getPlant(int plantId) {
    return plants.get(plantId);
  }
}
