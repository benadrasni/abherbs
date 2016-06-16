package sk.ab.herbs.backend.entity;

import com.googlecode.objectify.Key;
import com.googlecode.objectify.annotation.Entity;
import com.googlecode.objectify.annotation.Id;

import java.util.List;
import java.util.Map;

@Entity
public class Plant {

    @Id Integer plantId;
    String back_url;
    Integer height_from;
    Integer height_to;
    Integer flowering_from;
    Integer flowering_to;
    Integer toxicity_class;
    Map<String, String> title;
    Map<String, String> description;
    Map<String, String> flower;
    Map<String, String> inflorescence;
    Map<String, String> fruit;
    Map<String, String> leaf;
    Map<String, String> stem;
    Map<String, String> habitat;
    Map<String, String> trivia;
    Map<String, String> toxicity;
    Map<String, String> herbalism;

    Map<String, List<String>> names;
    Map<String, List<String>> photo_urls;
    Map<String, List<String>> source_urls;
}