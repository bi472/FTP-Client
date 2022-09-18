package clients;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import json.Json;
import json.JsonArray;
import json.JsonObject;
import json.JsonValue;
import json.WriterConfig;
import repository.DatabaseClientRepository;

public class DatabaseClient implements DatabaseClientRepository{

    @Override
    public void findAllStudents() {
        try {
            JsonObject jObject = Json.parse(new FileReader("database.json")).asObject();
            List<String> names = new ArrayList<>();
            JsonArray jArray = jObject.get("students").asArray();
            for (JsonValue item: jArray)
                names.add(item.asObject().getString("name","Unknown item"));
            Collections.sort(names);
            System.out.println("Список всех студентов, отсортированный по имени: ");
            for (String item: names)
                System.out.println(item);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }



    @Override
    public void findStudentById(int id) {
        try {
            JsonArray jArray = Json.parse(new FileReader("database.json")).asObject().get("students").asArray();
            boolean log = false;
            for (JsonValue item: jArray){
                if (id == item.asObject().getInt("id",123)){
                    log = true;
                    System.out.println("Студент с id[" + id + "]: " + item.asObject().getString("name","Unknown item"));
                    break;
                }
            }
            if (!log)
                System.out.println("Студент с данным id не найден.");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void addStudent(String name) {
        try {
            JsonArray jArray = Json.parse(new FileReader("database.json")).asObject().get("students").asArray();
            
            JsonObject jObject = Json.object().add("id", 5).add("name", name);
            jArray.add(jObject);
            JsonObject jsonObject = Json.object().add("students", jArray);
            OutputStream outputStream = new FileOutputStream("database.json", false);
            outputStream.write(jsonObject.toString(WriterConfig.PRETTY_PRINT).getBytes());
            outputStream.close();
        }
        catch(Exception exception){
            exception.printStackTrace();
        }
    }

    @Override
    public void deleteStudent(int id) {
        
        
    }
    
}
