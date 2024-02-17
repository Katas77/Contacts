package org.example;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.stream.Collectors;


@Component
@Slf4j
@PropertySource("classpath:application-init.yaml")
@Profile("init")
public class WorkingFileInit implements WorkingFile {
    InitSavePaths path;
    private ArrayList<Contact> contactsArrayList = new ArrayList<>();

    @Autowired
    public WorkingFileInit(InitSavePaths simplePath) {
        this.path = simplePath;
    }

    @PostConstruct
    public void initContacts() {
        File file = new File(path.getPathInit());
        try {
            BufferedReader reader = new BufferedReader(new FileReader(file));
            for (; ; ) {
                String[] contactOne = reader.readLine().split(";");
                contactsArrayList.add(new Contact(contactOne[0].trim(), contactOne[1].trim(), contactOne[2].trim().substring(0, contactOne[2].trim().length() - 1)));
                if (contactOne.length == 0) {
                    break;
                }
            }
        } catch (Exception exception) {
            exception.getMessage();
        }
        if (contactsArrayList.isEmpty()) {
            return;
        }
        ArrayList<String> newContactsStr = new ArrayList<>();
        contactsArrayList.forEach(contacts -> newContactsStr.add(contacts.getFullName() + ";" + contacts.getPhoneNumber() + ";" + contacts.getEmail() + "."));
        try {
            Files.write(Paths.get(path.getPathSave()), newContactsStr);
        } catch (Exception exception) {
            System.out.println(exception.getMessage());
        }
    }

    public void printContacts() {
        contactsArrayList = new ArrayList<>();
        ArrayList<Contact> printList = readContacts();
        if (printList.isEmpty()) {
            System.out.println("Список контактов пустой");
        } else {
            printList.forEach(System.out::println);
        }
    }

    public void addContacts(String contact) {
        contactsArrayList = new ArrayList<>();
        readContacts();
        String[] contactArr = contact.split(";");
        contactsArrayList.add(new Contact(contactArr[0].trim(), contactArr[1].trim(), contactArr[2].trim().substring(0, contactArr[2].trim().length() - 1)));
        recordingFile();
        System.out.println("Контакт    с " + contactArr[0].trim() + " добавлен ");
    }

    public void delContact(String email) {
        contactsArrayList = new ArrayList<>();
        readContacts();
        if (contactsArrayList.stream().noneMatch(contact -> contact.getEmail().equals(email))) {
            System.out.println("В списках нет контакта  с email - " + email);
            return;
        }
        String fullName = contactsArrayList.stream().filter(contact -> contact.getEmail().equals(email)).collect(Collectors.toList()).get(0).getFullName();
        contactsArrayList = (ArrayList<Contact>) contactsArrayList.stream().filter(contact -> !contact.getEmail().contains(email)).collect(Collectors.toList());
        recordingFile();
        System.out.println("Контакт  " + fullName + "  с email  " + email + "  удален ");
    }

    private void recordingFile() {
        ArrayList<String> newContactsStr = new ArrayList<>();
        contactsArrayList.forEach(contacts -> newContactsStr.add(contacts.getFullName() + ";" + contacts.getPhoneNumber() + ";" + contacts.getEmail() + "."));
        if (newContactsStr.isEmpty()) {
            return;
        }
        try {
            Files.write(Paths.get(path.getPathSave()), newContactsStr);
        } catch (Exception exception) {
            System.out.println(exception.getMessage());
        }
    }

    public ArrayList<Contact> readContacts() {
        File file = new File(path.getPathSave());
        try {
            BufferedReader reader = new BufferedReader(new FileReader(file));
            for (; ; ) {
                String[] contactOne = reader.readLine().split(";");
                contactsArrayList.add(new Contact(contactOne[0].trim(), contactOne[1].trim(), contactOne[2].trim().substring(0, contactOne[2].trim().length() - 1)));
                if (contactOne.length == 0) {
                    break;
                }
            }
        } catch (Exception exception) {
            System.out.println(exception.getMessage());
        }
        return contactsArrayList;
    }
}
