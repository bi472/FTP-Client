package clients;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.OutputStream;
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

    private static final String DATABASE_NAME = "database.json";
    private static final String ARRAY_NAME = "students";

    private void writeFile(JsonObject jsonObject){
        try{
        OutputStream outputStream = new FileOutputStream(DATABASE_NAME, false);
        outputStream.write(jsonObject.toString(WriterConfig.PRETTY_PRINT).getBytes());
        outputStream.close();
        }
        catch(Exception ex){
            ex.printStackTrace();
        }
    }

    @Override
    public void findAllStudents() {
        try {
            List<String> names = new ArrayList<>();
            JsonArray jArray = Json.parse(new FileReader(DATABASE_NAME)).asObject().get(ARRAY_NAME).asArray();
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

    private void addStudentInNullArray(String name){
        try {
            JsonArray jArray = Json.parse(new FileReader(DATABASE_NAME)).asObject().get(ARRAY_NAME).asArray();
            jArray.add(Json.object().add("id",1).add("name", name));
            JsonObject jsonObject = Json.object().add(ARRAY_NAME, jArray);
            writeFile(jsonObject);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    @Override
    public void findStudentById(int id) {
        try {
            JsonArray jArray = Json.parse(new FileReader(DATABASE_NAME)).asObject().get(ARRAY_NAME).asArray();
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
            JsonArray jArray = Json.parse(new FileReader(DATABASE_NAME)).asObject().get(ARRAY_NAME).asArray();
            List<Integer> idsInArrays = new ArrayList<>();
            for (JsonValue item : jArray){
                idsInArrays.add(item.asObject().getInt("id",123));
            }
            jArray.add(Json.object().add("id", idsInArrays.get(idsInArrays.size()- 1) + 1).add("name", name));
            JsonObject jsonObject = Json.object().add(ARRAY_NAME, jArray);
            writeFile(jsonObject);
        }
        catch(ArrayIndexOutOfBoundsException exception){
            addStudentInNullArray(name);
        } 
        catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void deleteStudent(int id) {
        try {
            JsonArray jArray = Json.parse(new FileReader(DATABASE_NAME)).asObject().get(ARRAY_NAME).asArray();
            boolean log = false;
            List<Integer> listIdInArray = new ArrayList<>();
            for (JsonValue item: jArray){
                int idInArray = item.asObject().getInt("id",123);
                listIdInArray.add(idInArray);
                if (id == item.asObject().getInt("id",123)){
                    log = true;
                    jArray.remove(listIdInArray.indexOf(idInArray));
                    break;
                }
            }
            if (!log)
                System.out.println("Студент с данным id не найден.");
            JsonObject jsonObject = Json.object().add(ARRAY_NAME, jArray);
            writeFile(jsonObject);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
}
