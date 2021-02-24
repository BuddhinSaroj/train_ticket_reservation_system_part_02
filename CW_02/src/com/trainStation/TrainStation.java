package com.trainStation;

import com.mongodb.BasicDBObject;
import com.mongodb.client.*;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;
import org.bson.Document;

import java.io.File;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Random;
import java.util.Scanner;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;

public class TrainStation extends Application  {


    private static ArrayList<Passenger> waitingRoom = new ArrayList<>();
    private int maximumWaitingTime = 0;
    private int minimumWaitingTime = 18;
    private double averageTime = 0.0;
    private int totalTime = 0;
    private int maxQueueLength = 0;
    ArrayList<Passenger> temp = new ArrayList<>();

    public static void main(String[] args) {
        launch();
    }

    @Override
    public void start(Stage primaryStage) {
        String [][][][] mainArray = new String[2][30][42][3];
        MongoClient mongoClient = MongoClients.create("mongodb://LocalHost:27017");
        MongoDatabase database = mongoClient.getDatabase("TrainBooking");
        MongoCollection<Document> collection = database.getCollection("Tickets");
        long dataCollection = database.getCollection("Tickets").countDocuments();
        FindIterable<Document> data = collection.find();
        for (Document record : data) {
            int trip = Integer.parseInt(record.get("Trip").toString());
            int seat = Integer.parseInt(record.get("Seat").toString());
            int date = Integer.parseInt(record.get("Date").toString());
            mainArray[trip][date][seat][0] = record.get("Date").toString(); //append datas to the main array
            mainArray[trip][date][seat][1] = record.get("F_Name").toString();
            mainArray[trip][date][seat][2] = record.get("S_Name").toString();
        }if(dataCollection != 0) {
            for (int i = 0; i < 2; i++) {
                for (int x = 0; x < 30; x++) {
                    for (int y = 0; y < 42; y++) {
                        if (mainArray[i][x][y][1] != null) {
                            Passenger passengerObj = new Passenger();
                            passengerObj.setName(mainArray[i][x][y][1], mainArray[i][x][y][2]);
                            passengerObj.setSeat(y + 1);
                            waitingRoom.add(passengerObj);
                            //waitingRoom[x] = passengerObj;
                        }
                    }
                }
            }
        }else{
            System.out.println("No data found");
        }
        Scanner sc = new Scanner(System.in);
        menu:
        while (true) {
            System.out.println("\n--------------------------------------------------------");
            System.out.println("Enter \"A\" to add passenger to the train queue.         |");
            System.out.println("Enter \"V\" to view train queue.                         |");
            System.out.println("Enter \"D\" to delete passenger from the train queue.    |");
            System.out.println("Enter \"S\" Save data in to plain text file.             |");
            System.out.println("Enter \"L\" Load data from the text file.                |");
            System.out.println("Enter \"R\" to run the simulation and produce the report.|");
            System.out.println("Enter \"Q\" to Quit the program.                         |");
            System.out.println("--------------------------------------------------------");
            System.out.println("Your option :- ");
            String option = sc.next();
            switch (option) {
                case "A":
                case "a":
                    addPassenger();
                    break;

                case "V":
                case "v":
                    viewTrainQueue();
                    break;

                case "D":
                case "d":
                    deletePassenger();
                    break;

                case "S":
                case "s":
                    saveData();
                    break;

                case "L":
                case "l":
                    loadData();
                    break;

                case "R":
                case "r":
                    simulation();
                    break;

                case "Q":
                case "q":
                    quit();

                default:
                    System.out.println("Invalid Input!Try again.");
            }
        }
    }

    private void addPassenger() {
        if (PassengerQueue.isFull()){
            System.out.println(" Passenger Queue is full! ");
        }else if (waitingRoom.size()!=0) {
            Stage gui = new Stage();
            AnchorPane guiAnchorpane = new AnchorPane();
            guiAnchorpane.setStyle("-fx-background-color: #17202A  ");
            GridPane firstPane = new GridPane();
            firstPane.setStyle("-fx-background-color: #17202A  ");
            Label waitingroom = new Label("Waiting Room");
            waitingroom.setStyle("-fx-text-fill: #F7F9F9 ");
            Label passengerQueue = new Label("Train Queue");
            passengerQueue.setStyle("-fx-text-fill: #F7F9F9 ");
            GridPane secondPane = new GridPane();
            secondPane.setStyle("-fx-background-color: #17202A  ");
            Button[] btn = new Button[42]; //button array

            Button btn_add = new Button("Add"); //create add button and set styles
            btn_add.setDisable(true);
            btn_add.setPrefHeight(30);
            btn_add.setPrefWidth(100.0);
            btn_add.setStyle("-fx-background-color:#196F3D");
            btn_add.setLayoutX(150);
            btn_add.setLayoutY(900);

            Button btn_delete = new Button("Delete"); //ceate delete button and set styles
            btn_delete.setDisable(true);
            btn_delete.setPrefHeight(30.0);
            btn_delete.setPrefWidth(100.0);
            btn_delete.setStyle("-fx-background-color: #c70000");
            btn_delete.setLayoutX(250);
            btn_delete.setLayoutY(900);

            Button btn_ok = new Button("OK");
            btn_ok.setPrefHeight(30);
            btn_ok.setPrefWidth(195.0);
            btn_ok.setStyle("-fx-background-color:#2874A6");
            btn_ok.setLayoutX(500);
            btn_ok.setLayoutY(900);

            firstPane.setPrefSize(500, 800);
            firstPane.setLayoutX(150);
            firstPane.setLayoutY(75);
            firstPane.setHgap(15);
            firstPane.setVgap(15);
            AtomicReference<Passenger> variable = new AtomicReference<>(waitingRoom.get(0));
            AtomicReference<Button> variableBtn = new AtomicReference<>(btn[0]); //store clicked button
            int count = 0;
            for (Passenger passenger : waitingRoom) {
                if (passenger == null) continue;{
                    btn[count] = new Button("Name : " + passenger.getName() + "\nSeat No : " + passenger.getSeat());
                    btn[count].setPrefSize(195, 180);
                    btn[count].setStyle("-fx-background-color: #F1C40F  ");
                    int finalCount = count;
                    btn[count].setOnAction(event -> {
                        if (variableBtn.get()!=null) variableBtn.get().setStyle("-fx-background-color: #F1C40F  ");
                        btn[finalCount].setStyle("-fx-background-color: #17A589 ");
                        variable.set(passenger);
                        variableBtn.set(btn[finalCount]); //store clicked button in variableBtn
                        btn_add.setDisable(false);
                        btn_delete.setDisable(false);
                    });
                    count++;
                }
            }
            int num = 0;
            for (int a = 0; a < 1; a++) {
                for (int b = 0; b < count; b++) {
                    firstPane.add(btn[num++], a, b);//allign the created checkboxes in GridPane.
                }
            }
            Random rand = new Random();
            int rand_no = rand.nextInt(6)+1;
            secondPane.setPrefSize(500, 800);
            secondPane.setLayoutX(500);
            secondPane.setLayoutY(75);
            secondPane.setHgap(15);
            secondPane.setVgap(15);
            Button[] pasBtn = new Button[6];
            int count3 = 0;
            while (count3 < rand_no) { //create the check boxes
                pasBtn[count3] = new Button("Passenger : " + (count3 + 1));
                pasBtn[count3].setPrefSize(195, 180);
                pasBtn[count3].setStyle("-fx-background-color: #B3B6B7 ");
                count3++;
            }
            int num3 = 0;
            for (int x = 0; x < 6; x++) {
                for (int y = 0; y < 1; y++) {
                    if (num3 == rand_no) break;
                    secondPane.add(pasBtn[num3++], y, x);//allign the created checkboxes in GridPane.
                }
            }
            AtomicInteger passengerCount= new AtomicInteger();
            btn_ok.setOnAction(event -> {
                gui.close();
            });
            btn_add.setOnAction(event -> {
                pasBtn[passengerCount.get()].setStyle("-fx-background-color: #1D8348  ");
                PassengerQueue.addToQueue(variable.get());
                waitingRoom.remove(variable.get());
                passengerCount.getAndIncrement();
                variableBtn.get().setDisable(true);
                variableBtn.get().setStyle("-fx-background-color: #1D8348  ");
                variableBtn.set(null);
                btn_add.setDisable(true);
                btn_delete.setDisable(true);
                if (passengerCount.get()==rand_no){gui.close();}
            });
            btn_delete.setOnAction(event -> {
                pasBtn[passengerCount.get()].setStyle("-fx-background-color: #A93226 ");
                waitingRoom.remove(variable.get());
                passengerCount.getAndIncrement();
                variableBtn.get().setDisable(true);
                variableBtn.get().setStyle("-fx-background-color: #A93226 ");
                variableBtn.set(null);
                btn_add.setDisable(true);
                btn_delete.setDisable(true);
                if (passengerCount.get()==rand_no){gui.close();}
            });
            waitingroom.setLayoutY(30);
            waitingroom.setLayoutX(215);
            passengerQueue.setLayoutY(30);
            passengerQueue.setLayoutX(570);
            guiAnchorpane.getChildren().addAll(firstPane, secondPane, waitingroom, passengerQueue, btn_add, btn_delete, btn_ok);
            Scene scene = new Scene(guiAnchorpane, 900, 950);
            gui.setScene(scene);
            gui.setTitle("Waitiing Room");
            gui.showAndWait();
        }else {
            System.out.println("************************************************");
            System.out.println("*************   No Data to view!   *************");
            System.out.println("************************************************");
        }
    }

    private static void viewTrainQueue() {
        Stage gui = new Stage();
        AnchorPane guiAnchorpane = new AnchorPane();
        guiAnchorpane.setStyle("-fx-background-color: #17202A  ");
        GridPane firstPane = new GridPane();
        firstPane.setStyle("-fx-background-color: #17202A  ");
        Label pasengerQueue = new Label("Passenger Queue");
        pasengerQueue.setStyle("-fx-text-fill: #F7F9F9 ");
        Button[] btn = new Button[42];
        firstPane.setPrefSize(900, 800);
        firstPane.setLayoutX(50);
        firstPane.setLayoutY(75);
        firstPane.setHgap(15);
        firstPane.setVgap(15);
        PassengerQueue trainQueue = new PassengerQueue();
        Passenger[] myArray ;
        myArray = trainQueue.getQueueArray();
        int count = 0;
        while (count < 42) { //create the check boxes
            if(myArray[count] != null){
                btn[count] = new Button("Name : "+myArray[count].getName()+"\nSeat No : "+myArray[count].getSeat());
                btn[count].setStyle("-fx-background-color: #F1C40F  ");
                //btn[count].setDisable(true);
            }
            else{
                btn[count] = new Button("Empty");
                btn[count].setStyle("-fx-background-color: #c70000  ");
                //btn[count].setDisable(true);
            }
            btn[count].setPrefSize(195, 180);
            count++;
        }
        int num = 0;
        for (int i = 0; i < 7; i++) {
            for (int j = 0; j < 6; j++) {
                firstPane.add(btn[num++], j, i);//allign the created checkboxes in GridPane.
            }
        }
        pasengerQueue.setLayoutY(30);
        pasengerQueue.setLayoutX(440);
        guiAnchorpane.getChildren().addAll(firstPane,pasengerQueue);
        Scene scene = new Scene(guiAnchorpane, 1000, 950);
        gui.setScene(scene);
        gui.setTitle("Passenger queue");
        gui.showAndWait();
    }

    private static void deletePassenger() {
        PassengerQueue trainQueue = new PassengerQueue();
        Scanner sc = new Scanner(System.in);
        System.out.println("Seat No : ");
        int no = sc.nextInt();
        try {
            for (int i = 0; i < 42; i++) {
                if (trainQueue.getQueueArray()[i].getSeat() == no) {
                    trainQueue.remove(i);
                    break;
                }
            }
            for (int x = 0; x < 41; x++) {
                if (trainQueue.getQueueArray()[x] == null) {
                    trainQueue.getQueueArray()[x] = trainQueue.getQueueArray()[x + 1];
                    trainQueue.getQueueArray()[x + 1] = null;
                }
            }
            System.out.println("***************Deleted succesfull!**************");
        } catch (Exception e) {
            System.out.println("************************************************");
            System.out.println("*************   No Seats Detected  *************");
            System.out.println("************************************************");
        }

    }

    private static void saveData() {
        MongoClient mongoClient = MongoClients.create("mongodb://LocalHost:27017");
        MongoDatabase database = mongoClient.getDatabase("TrainBookingxx");
        MongoCollection<Document> collection = database.getCollection("Ticketsxx");
        BasicDBObject document = new BasicDBObject();
        collection.deleteMany(document); //delete previous datas
        for(Passenger passenger : PassengerQueue.getQueueArray()){
            if (passenger != null){
                Document record = new Document("Title","name")
                        .append("name",passenger.getName())
                        .append("seat",passenger.getSeat());
                collection.insertOne(record);
            }
        }
        System.out.println("************************************************");
        System.out.println("************  Succesfully Stored!   ************");
        System.out.println("************************************************");
    }

    private static void loadData() {
        MongoClient mongoClient = MongoClients.create("mongodb://LocalHost:27017");
        MongoDatabase database = mongoClient.getDatabase("TrainBookingxx");
        MongoCollection<Document> collection = database.getCollection("Ticketsxx");
        BasicDBObject document = new BasicDBObject();
        FindIterable<Document> data = collection.find();
        PassengerQueue passenger = new PassengerQueue();
        for (Document record : data) {
            String name = record.get("name").toString();
            int seat = Integer.parseInt(record.get("seat").toString());
            //System.out.println(Integer.toString(seat));
            Passenger obj = new Passenger();
            obj.setName(name,"");
            obj.setSeat(seat);
            PassengerQueue.addToQueue(obj);
            for(Passenger passenger1 : waitingRoom){
                if(passenger1.getSeat() == seat){
                    waitingRoom.remove(passenger1);
                    break;
                }
            }
        }
        System.out.println("************************************************");
        System.out.println("************  Loading Succesfull!   ************");
        System.out.println("************************************************");

    }

    private void simulation() {
        PassengerQueue trainQueue = new PassengerQueue();
        Passenger[] myArray ;
        myArray = trainQueue.getQueueArray();

       for(int a = 0 ; a<42 ; a++){
           if(myArray[a] != null){
               temp.add(myArray[a]);
           }
       }
       if(temp.size() != 0) {
           Random rand = new Random();
           int queueLength = 0;
           for (int x = 0; x < 42; x++) {
               int dice_01 = rand.nextInt(6) + 1;
               int dice_02 = rand.nextInt(6) + 1;
               int dice_03 = rand.nextInt(6) + 1;
               int dice_total = dice_01 + dice_02 + dice_03;
               if (myArray[x] != null) {
                   myArray[x].setSecondsInQueue(dice_total);
                   totalTime += dice_total;
                   myArray[x].setTotalSeconds(totalTime);
                   queueLength++;

                   if (maximumWaitingTime < dice_total) {
                       maximumWaitingTime = dice_total;
                   }
                   if (minimumWaitingTime > dice_total) {
                       minimumWaitingTime = dice_total;
                   }
               }
           }
           try {
               if (queueLength > maxQueueLength) {
                   maxQueueLength = queueLength;
                   trainQueue.setMaxLength(maxQueueLength);
               }

               averageTime = totalTime / temp.size();

               Stage gui = new Stage();
               AnchorPane guiAnchorpane = new AnchorPane();
               guiAnchorpane.setStyle("-fx-background-color: #17202A  ");
               GridPane firstPane = new GridPane();
               firstPane.setStyle("-fx-background-color: #17202A  ");
               Label pasengerQueue = new Label("Train Board");
               pasengerQueue.setStyle("-fx-text-fill: #F7F9F9 ");
               Button[] btn = new Button[42];
               firstPane.setPrefSize(900, 800);
               firstPane.setLayoutX(50);
               firstPane.setLayoutY(75);
               firstPane.setHgap(15);
               firstPane.setVgap(15);
               int count = 0;
               for(int i = 0 ; i<temp.size() ; i++) { //create the check boxes
                   btn[count] = new Button("Name : " + temp.get(count).getName() + "\nSeat No : " + temp.get(count).getSeat() + "\nProcessing Time: "
                           + temp.get(count).getSecondsInQueue()+"\nWaiting time: "+temp.get(count).getTotalSeconds());
                   //btn[count].setDisable(true);
                   btn[count].setStyle("-fx-background-color: #F1C40F  ");
                   btn[count].setPrefSize(195, 180);
                   count++;
               }
               int buttons = 0;
               int num = 0;
               for (int i = 0; i < 7; i++) {
                   for (int j = 0; j < 6; j++) {
                       firstPane.add(btn[num++], i, j);//allign the created checkboxes in GridPane.
                       buttons++;
                       if(buttons == temp.size()){
                           break;
                       }
                   }
                   if(buttons == temp.size()) {
                       break;
                   }
               }
               Label myTxt = new Label("Maximum length of Queue : " + trainQueue.getMaxLength()+"\nMaximum Waiting time : " + maximumWaitingTime+"\nMinimum Waiting time : "
                       + minimumWaitingTime +"\nAverage Waiting time : " + averageTime);
               myTxt.setStyle("-fx-text-fill: #F7F9F9 ");
               myTxt.setLayoutX(910);
               myTxt.setLayoutY(300);
               pasengerQueue.setLayoutY(30);
               pasengerQueue.setLayoutX(440);
               guiAnchorpane.getChildren().addAll(firstPane, pasengerQueue,myTxt);
               Scene scene = new Scene(guiAnchorpane, 1200, 950);
               gui.setScene(scene);
               gui.setTitle("Train Board");
               System.out.println("************************************************");
               System.out.println("***Your program is in process,Wait " + maximumWaitingTime + " seconds***");
               System.out.println("************************************************");
               TimeUnit.SECONDS.sleep(maximumWaitingTime);
               gui.showAndWait();

               File txtFile = new File("trainBoard");
               if (!txtFile.exists()) {
                   txtFile.createNewFile();
               }
               PrintWriter writer = new PrintWriter(txtFile);
               for (int i = 0; i < temp.size(); i++) {
                   writer.println("Name : " + temp.get(i).getName());
                   writer.println("Seat No : " + temp.get(i).getSeat());
                   writer.println("Processing time : " + temp.get(i).getSecondsInQueue());
                   writer.println("Waiting time : " + temp.get(i).getTotalSeconds());
                   writer.println("");
               }
               writer.println("******************************");
               writer.println("Maximum length of Queue : " + trainQueue.getMaxLength());
               writer.println("Maximum Waiting time : " + maximumWaitingTime);
               writer.println("Minimum Waiting time : " + minimumWaitingTime);
               writer.println("Average Waiting time : " + averageTime);
               writer.println("******************************");
               writer.close();

           } catch (Exception e) {
               System.out.println("An error ocured,Please try again.");
           }
           for (int i = 0; i < 42; i++) {
               myArray[i] = null;
           }
       }else {
           System.out.println("************************************************");
           System.out.println("*************No Passengers to board*************");
           System.out.println("************************************************");
       }
       trainQueue.setLast(0);

    }

    private void quit() {
        System.out.println("Your program is now executed.Thank You!");
        System.exit(0);
    }

}


