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

public class DatabaseClient implements DatabaseClientRepository {

    private static final String DATABASE_NAME = "database.json";
    private static final String ARRAY_NAME = "students";

    public static final String ANSI_RESET = "\u001B[0m";
    public static final String ANSI_GREEN = "\u001B[32m";
    public static final String ANSI_BLUE = "\u001B[34m";
    public static final String ANSI_RED = "\u001B[31m";
    
    private JsonArray getArray() throws FileNotFoundException, IOException{
        return Json.parse(new FileReader(DATABASE_NAME)).asObject().get(ARRAY_NAME).asArray();
    }

    private void writeFile(JsonObject jsonObject) {
        try {
            OutputStream outputStream = new FileOutputStream(DATABASE_NAME, false);
            outputStream.write(jsonObject.toString(WriterConfig.PRETTY_PRINT).getBytes());
            outputStream.close();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    @Override
    public void findAllStudents() {
        try {
            JsonArray jArray = getArray();
            List<String> names = new ArrayList<>();
            if (jArray.values().size() != 0){
            for (JsonValue item : jArray)
                names.add(item.asObject().getString("name", "Unknown item"));
            Collections.sort(names);
            System.out.println(ANSI_BLUE + "Список всех студентов, отсортированный по имени: ");
            for (String item : names)
                System.out.println(ANSI_GREEN + item);
            System.out.print(ANSI_RESET);
            }
            else
                System.out.println(ANSI_RED + "Список со студентами пуст!" + ANSI_RESET);
        } catch (Exception e) {
            System.out.println(e.toString());
        }
    }

    private void addStudentInNullArray(String name) {
        try {
            JsonArray jArray = getArray();
            jArray.add(Json.object().add("id", 1).add("name", name));
            JsonObject jsonObject = Json.object().add(ARRAY_NAME, jArray);
            writeFile(jsonObject);
            System.out.println(ANSI_GREEN + "Студент с именем " + name + " успешно добавлен!" + ANSI_RESET);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void findStudentById(int id) {
        try {
            JsonArray jArray = getArray();
            boolean log = false;
            for (JsonValue item : jArray) {
                if (id == item.asObject().getInt("id", 123)) {
                    log = true;
                    System.out
                            .println(ANSI_GREEN + "Студент с id = " + id + " : " + item.asObject().getString("name", "Unknown item") + ANSI_RESET);
                    break;
                }
            }
            if (!log)
                System.out.println(ANSI_RED + "Студент с данным id не найден." + ANSI_RESET);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void addStudent(String name) {
        try {
            if (!name.isEmpty()) {
                JsonArray jArray = getArray();
                List<Integer> idsInArrays = new ArrayList<>();
                for (JsonValue item : jArray) {
                    idsInArrays.add(item.asObject().getInt("id", 123));
                }
                // Автоинкремент по id записи
                jArray.add(Json.object().add("id", idsInArrays.get(idsInArrays.size() - 1) + 1).add("name", name));
                JsonObject jsonObject = Json.object().add(ARRAY_NAME, jArray);
                writeFile(jsonObject);
                System.out.println(ANSI_GREEN + "Студент с именем " + name + " успешно добавлен!" + ANSI_RESET);
            }
            else {
                System.out.println(ANSI_RED + "Поле name не может быть пустым." + ANSI_RESET);
            }
        } catch (ArrayIndexOutOfBoundsException exception) {
            addStudentInNullArray(name);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void deleteStudent(int id) {
        try {
            JsonArray jArray = getArray();
            boolean log = false;
            List<Integer> listIdInArray = new ArrayList<>();
            for (JsonValue item : jArray) {
                int idInArray = item.asObject().getInt("id", 123);
                listIdInArray.add(idInArray);
                if (id == item.asObject().getInt("id", 123)) {
                    log = true;
                    jArray.remove(listIdInArray.indexOf(idInArray));
                    break;
                }
            }
            if (!log)
                System.out.println(ANSI_RED + "Студент с данным id не найден." + ANSI_RESET);
            else
                System.out.println(ANSI_GREEN + "Студент с данным id удалён." + ANSI_RESET);
            JsonObject jsonObject = Json.object().add(ARRAY_NAME, jArray);
            writeFile(jsonObject);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
