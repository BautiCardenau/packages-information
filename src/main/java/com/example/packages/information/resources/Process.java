package com.example.packages.information.resources;

import java.awt.*;
import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;

public class Process {

    ArrayList<String> packages = new ArrayList<String>();
    ArrayList<String> descriptions = new ArrayList<String>();
    HashMap<String, ArrayList<String>> dependencies = new HashMap<>();
    HashMap<String, ArrayList<String>> revDependencies = new HashMap<>();

    String packageStart = "Package: ";
    String descriptionStart = "Description: ";
    String dependenciesStart = "Depends: ";

    String packageName = "";

    boolean isLinux = true;

    BufferedReader reader;

    public void processFile (String filePath) {
        try {
            reader = new BufferedReader(new FileReader(filePath));
            String line = reader.readLine();
            while (line != null) {
                System.out.println(line);

                if (line.startsWith(packageStart)) {
                    packageName = line.substring(packageStart.length()).trim();
                    packages.add(packageName);
                }
                if (line.startsWith(descriptionStart)) {
                    descriptions.add(line.substring(descriptionStart.length()).trim());
                }
                if (line.startsWith(dependenciesStart)) {
                    String subline = line.substring(dependenciesStart.length());
                    String[] dependenciesWithVersion = subline.split(", ");
                    ArrayList<String> dependenciesWOVersion = new ArrayList<String>();
                    String currentDependency;

                    for (String dependencyWithVersion : dependenciesWithVersion) {
                        ArrayList<String> currentRevDependencies = new ArrayList<String>();
                        int index = dependencyWithVersion.indexOf("(");
                        currentRevDependencies.clear();

                        //Take the version out of the package name
                        if (index != -1) {
                            currentDependency = dependencyWithVersion.substring(0, index);
                        } else {
                            currentDependency = dependencyWithVersion;
                        }
                        currentDependency.trim();
                        dependenciesWOVersion.add(currentDependency);

                        //Check for previous reverse dependencies and save them
                        if (revDependencies.get(currentDependency) != null) {
                            currentRevDependencies = revDependencies.get(currentDependency);
                        }
                        currentRevDependencies.add(packageName);
                        revDependencies.put(currentDependency, currentRevDependencies);
                    }
                    dependencies.put(packageName, dependenciesWOVersion);
                }

                line = reader.readLine();
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            isLinux = false;
        }
    }

    public String createHtml() throws Exception {

        File htmlFile = new File("src/packages.html");

        String html = "<html>\n" +
                " <head>Information about packages</head>\n" +
                " <body>";

        int i = 0;

        for (String packag : packages){

            html += "<div id=" + packag + "><pre>" + packageStart + packag + "\n" +
                    descriptionStart + descriptions.get(i) + "\n" +
                    dependenciesStart;

            if (dependencies.get(packag) != null){
                for (String dependency : dependencies.get(packag)){
                    if(!dependency.equals(dependencies.get(packag).get(dependencies.get(packag).size()-1))){
                        html += "<a href=\"#" + dependency + "\">" + dependency + "</a>, ";
                    }else{
                        html += "<a href=\"#" + dependency + "\">" + dependency + "</a>";
                    }
                }
            } else {
                html += " - ";
            }

            html += "\nReverse Dependencies: ";

            if (revDependencies.get(packag) != null){
                for (String revDependency : revDependencies.get(packag)){
                    if(!revDependency.equals(revDependencies.get(packag).get(revDependencies.get(packag).size()-1))){
                        html += "<a href=\"#" + revDependency + "\">" + revDependency + "</a>, ";
                    }else{
                        html += "<a href=\"#" + revDependency + "\">" + revDependency + "</a>";
                    }
                }
            } else {
                html += " - ";
            }

            html += "\n </pre></div>";

            i++;

            }

        html += "</body> \n </html>";
        
        return html;
//        BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter(htmlFile));
//        bufferedWriter.write(html);
//        bufferedWriter.close();
//
//        Desktop.getDesktop().browse(htmlFile.toURI());
    }

}
