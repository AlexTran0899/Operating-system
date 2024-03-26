// Created by Alex Tran
// Uark ID: 010796179

import java.util.concurrent.Semaphore;

public class Customer implements Runnable {
  private Semaphore waiting_area_seat;
  private Semaphore num_barber_available;
  private boolean is_in_waiting_area = false;
  private boolean did_customer_left_in_frustration = false;
  private int customer_id;

  Customer(Semaphore waiting_area_seat, Semaphore num_barber_available,int customer_id) {
    this.waiting_area_seat = waiting_area_seat;
    this.num_barber_available = num_barber_available;
    this.customer_id = customer_id;
  }

  private boolean getWait() {
    if(num_barber_available.availablePermits() > 0){
      BarberShop.num_barber_available_lock.lock();
      System.out.println("customer " + customer_id + " went directly to the barbar chair" + ", we have " +  (++BarberShop.barber_counter) + " performing haircut");
      BarberShop.num_barber_available_lock.unlock();

    } else if(waiting_area_seat.availablePermits() > 0) {
      BarberShop.waiting_counter_lock.lock();
      System.out.println("customer " + customer_id + " enters the waiting room" + ", we have " + (++BarberShop.waiting_counter) + " in the waiting area." );
      waiting_area_seat.tryAcquire();
      is_in_waiting_area = true;
      BarberShop.waiting_counter_lock.unlock();
    } else {
      did_customer_left_in_frustration = true;
      return false;
    }
    return true;
  }

  private void getBarbar(){
    if(did_customer_left_in_frustration) return;
    try {
      num_barber_available.acquire(); // wait until we find an available barbar then continue with this code.
      if(is_in_waiting_area) {
        BarberShop.num_barber_available_lock.lock();
        BarberShop.waiting_counter_lock.lock();
        System.out.println( "customer " + customer_id + " moves from the waiting area to barbar chair" + ", we have " + (++BarberShop.barber_counter) + " performing haircut and " + (--BarberShop.waiting_counter)  + " waiting seat available.");
        waiting_area_seat.release();
        BarberShop.waiting_counter_lock.unlock();
        BarberShop.num_barber_available_lock.unlock();

      }
//      Thread.sleep(BarberShop.getRandomNumber());
      Thread.sleep(10000);
    }catch(InterruptedException ignore) {}
  }

  private void exitShop() {
    if(did_customer_left_in_frustration){
      System.out.println("Customer " + customer_id + " left in frustration" + ", we have " +  num_barber_available.availablePermits() + " barber(s) available and " + waiting_area_seat.availablePermits()  + " waiting seat available.");
    } else {
      BarberShop.num_barber_available_lock.lock();
      System.out.println("Customer " + customer_id + " paid and left the shop");
      --BarberShop.barber_counter;
      num_barber_available.release();
      BarberShop.num_barber_available_lock.unlock();
    }
  }

  @Override
  public void run() {
    getWait();
    getBarbar();
    exitShop();
  }
}

