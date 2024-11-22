package com.example.area_backend.TableDb.HandleJson;

import org.springframework.stereotype.Service;

import com.example.area_backend.TableDb.EnumServices;

import java.util.List;
import java.util.Optional;
import java.util.ArrayList;

import org.springframework.beans.factory.annotation.Autowired;

@Service
public class HandleJsonService {

    HandleJsonRepo handleJsonRepo;
    HandleJsonTable handleJsonTable;

    @Autowired
    public HandleJsonService(HandleJsonRepo handleJsonRepo)
    {
        this.handleJsonRepo = handleJsonRepo;
    }

    public List<HandleJsonTable> getAllHandleJson()
    {
        return this.handleJsonRepo.findAll();
    }

    public Optional<HandleJsonTable> createHandleJson(HandleJsonTable handleJson)
    {
        try {
            this.handleJsonTable = this.handleJsonRepo.save(handleJson);
        } catch (Exception e) {
            System.err.println(e);
            return Optional.ofNullable(null);
        }
        System.out.println("HandleJson with id: " + this.handleJsonTable.getId() +  " saved successfully");
        return Optional.of(this.handleJsonTable);
    }

    /*
     * For get all by type and by service
     * type = "Action" or "Reaction"
     * services = EnumService (put DISCORD when you only getByType)
     * both = 0 is to getAllByType and 1 is to getAllByTypeByService
    */
    public List<HandleJsonTable> getAllByTypeByService(String type, EnumServices services, int both)
    {
        List<HandleJsonTable> handleJsonList;
        List<HandleJsonTable> resultHandleJsonList = new ArrayList<>();
        handleJsonList = this.handleJsonRepo.findAll();
        int sizeList = handleJsonList.size();
        for (int i = 0; i < sizeList; i++) {
            handleJsonTable = handleJsonList.get(i);
            if (type == null || handleJsonTable.getType().equals(type)) {
                if (both == 0 || handleJsonTable.getService().equals(services))
                    resultHandleJsonList.add(handleJsonTable);
            }
        }
        return (resultHandleJsonList);
    }
}
