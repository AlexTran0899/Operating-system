// Created by Alex Tran
// Uark ID: 010796179

import java.io.*;
import java.util.ArrayList;
import java.util.Random;
import java.util.concurrent.Semaphore;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class BarberShop {
  static int barber_counter = 0;
  static Lock num_barber_available_lock = new ReentrantLock();
  static int waiting_counter = 0;
  static Lock waiting_counter_lock = new ReentrantLock();

  public static void main(String[] args) {
    long start_time = System.nanoTime();
    long elapsed_time_in_seconds = 0;
    int run_duration_in_seconds = Integer.parseInt(args[0]);
//    int total_customers_remaining = Integer.parseInt(args[1]);
    int total_customers_remaining = Integer.parseInt(args[1]) * 5;

    Semaphore num_barber_available = new Semaphore(Integer.parseInt(args[1]));
    Semaphore waiting_area_seat = new Semaphore(Integer.parseInt(args[1]) * 2);
    ArrayList<Thread> threads = new ArrayList<Thread>();

    System.out.println("Please open the output.txt file to read the log");
    System.out.println("Note that the log is not complete until the program stop executing \n");
    System.out.println("If you can't find the output.txt file, you may need to refresh your file navigator or type 'ls' again");

    try {
      PrintStream out = new PrintStream(new FileOutputStream("output.txt"));
      System.setOut(out);
      System.out.println("There are 2 version of this application, the default one does not follow the rubric but aim to demonstrate how the program would handle edge cases");
      System.out.println("The second one attempt to follow the rubric and can be set by swapping line 21 for 22 in 'BarberShop' file and swapping out line 51 for 52 in 'Customer' file ");

    } catch (FileNotFoundException e) {
      System.err.println("Exception caught: " + e.getMessage());
    }

    while(elapsed_time_in_seconds < run_duration_in_seconds && total_customers_remaining > 0) {
      elapsed_time_in_seconds = (System.nanoTime() - start_time) / 1_000_000_000;
      total_customers_remaining -= 1;

      try {
        Thread.sleep(getRandomNumber());
        Customer c = new Customer(waiting_area_seat,num_barber_available, total_customers_remaining);
        Thread t = new Thread(c);
        System.out.println("Customer: " + total_customers_remaining + " enter the shop.");
        t.start();
      } catch(InterruptedException ignore) {}
    }
    System.out.println("*** At this point no more customer can enter the shop ***");

    for(int i = 0; i < threads.size(); i++){
      try{
        threads.get(i).join();
      }catch(InterruptedException ignore){}
    }



  }

  public static int getRandomNumber() {
    Random random = new Random();
    double randomNumber = random.nextDouble();  // This will generate a random number between 0.0 (inclusive) and 1.0 (exclusive)
    return (int)(randomNumber * 1000);
  }
}
