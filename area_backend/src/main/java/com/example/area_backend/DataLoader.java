package com.example.area_backend;
import java.io.File;
import java.io.FileNotFoundException;
import java.time.LocalDate;
import java.util.Scanner;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

import com.example.area_backend.TableDb.Credentials.CredentialsRepo;
import com.example.area_backend.TableDb.Credentials.CredentialsTable;
import com.example.area_backend.TableDb.EnumRoles;
import com.example.area_backend.TableDb.EnumServices;
import com.example.area_backend.TableDb.HandleJson.HandleJsonRepo;
import com.example.area_backend.TableDb.HandleJson.HandleJsonTable;
import com.example.area_backend.TableDb.Users.UsersRepo;
import com.example.area_backend.TableDb.Users.UsersTable;

@Component
public class DataLoader
{
    @Value("${spring.security.admin.name}")
    private String admin_name;
    @Value("${spring.security.admin.surname}")
    private String admin_surname;
    @Value("${spring.security.admin.email}")
    private String admin_email;
    @Value("${spring.security.admin.date}")
    private String admin_date;
    @Value("${spring.security.admin.phoneNumber}")
    private String admin_phone_number;
    @Value("${spring.security.admin.gender}")
    private String admin_gender;
    @Value("${spring.security.admin.password}")
    private String admin_password;

    private final UsersRepo userRepository;
    private final CredentialsRepo credentailRepository;
    private final HandleJsonRepo handleJsonRepository;

    public DataLoader(UsersRepo userRepository, CredentialsRepo credentailRepository, HandleJsonRepo handleJsonRepo) {
        this.userRepository = userRepository;
        this.credentailRepository = credentailRepository;
        this.handleJsonRepository = handleJsonRepo;
    }

    @Bean
    public CommandLineRunner loadData() {
        return args -> {
            if (this.userRepository.count() == 0) {
                UsersTable user = new UsersTable();
                user.setName(admin_name);
                user.setSurname(admin_surname);
                user.setEmail(admin_email);
                user.setDateOfBirth(LocalDate.parse(admin_date));
                user.setPhoneNumber(admin_phone_number);
                user.setGender(admin_gender);
                user.setRole(EnumRoles.ADMIN);
                this.userRepository.save(user);
                if (this.credentailRepository.count() == 0) {
                    CredentialsTable cdt = new CredentialsTable();
                    cdt.setEmail(admin_email);
                    cdt.setPassword(admin_password);
                    cdt.setUser(user);
                    this.credentailRepository.save(cdt);
                }
            }
            if (this.handleJsonRepository.count() == 0) {
                this.loadFileHandleJson();
            }
        };
    }

    private void loadFileHandleJson()
    {
        try {
            File myObj = new File("./src/main/resources/ActionsREActions.txt");
            Scanner myReader = new Scanner(myObj);
            while (myReader.hasNextLine()) {
                String data = myReader.nextLine();
                String[] value = data.split(";");
                HandleJsonTable newHandle = new HandleJsonTable(
                    null, EnumServices.valueOf(value[0]), value[1], value[2], value[3], value[4], value[5]
                );
                this.handleJsonRepository.save(newHandle);
            }
            myReader.close();
        } catch (FileNotFoundException e) {
            System.out.println("An error occurred with file ActionsREActions.");
            e.printStackTrace();
        }
    }

}
