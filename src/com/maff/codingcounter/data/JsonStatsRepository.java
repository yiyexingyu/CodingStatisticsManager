package com.maff.codingcounter.data;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonSyntaxException;

import java.io.IOException;
import java.nio.file.*;

public class JsonStatsRepository implements StatsRepository {

    private String fileName;
    private Gson gson;

    public JsonStatsRepository(String fileName){
        this.fileName = fileName;
        this.gson = (new GsonBuilder()).setPrettyPrinting().create();
    }

    @Override
    public CodingStats load() throws IOException {
        Path file = Paths.get(this.fileName);

        if(!Files.exists(file, new LinkOption[0])){
            return new CodingStats();
        } else {
            String json = new String(Files.readAllBytes(file));

            try {
                return (CodingStats)this.gson.fromJson(json, CodingStats.class);
            } catch (JsonSyntaxException e){
                throw new IOException("无法解析JSON数据为CodingStats", e);
            }
        }
    }

    @Override
    public void save(CodingStats var1) throws IOException {
        Path file = Paths.get(this.fileName);

        if(!Files.exists(file, new LinkOption[0])){
            Files.createDirectories(file.getParent());

            try{
                Files.createFile(file);
            } catch (FileAlreadyExistsException ignored){}

            String json = this.gson.toJson(var1);
            Files.write(file, json.getBytes(), new OpenOption[0]);
        }
    }
}

