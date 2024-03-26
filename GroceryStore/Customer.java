import java.util.Random;
import java.util.concurrent.Semaphore;
import java.util.concurrent.atomic.AtomicInteger;

public class Customer implements Runnable {
  int customer_id;
  Semaphore waiting_area;
  Semaphore produce_area;
  Semaphore general_area;
  Semaphore frozen_area;
  Semaphore cash_register;
  AtomicInteger waiting_area_atomic_int;
  AtomicInteger produce_area_atomic_int;
  AtomicInteger general_area_atomic_int;
  AtomicInteger frozen_area_atomic_int;
  AtomicInteger cash_register_atomic_int;
  Random random;

  public Customer(int customer_id, GroceryStoreSemaphores s, GroceryStoreAtomicIntegers m) {
    this.customer_id = customer_id;
    this.waiting_area = s.waiting_area;
    this.produce_area = s.produce_area;
    this.general_area = s.general_area;
    this.frozen_area = s.frozen_area;
    this.cash_register = s.cash_register;
    this.waiting_area_atomic_int = m.waiting_area_atomic_int;
    this.produce_area_atomic_int = m.produce_area_atomic_int;
    this.general_area_atomic_int = m.general_area_atomic_int;
    this.frozen_area_atomic_int = m.frozen_area_atomic_int;
    this.cash_register_atomic_int = m.cash_register_atomic_int;
    this.random = new Random(0);
  }

// Get waiting room
  private void getWaitingRoom() {
    try {
      waiting_area.acquire();
      System.out.println("customer " + customer_id + " enters the waiting area, there are " + waiting_area_atomic_int.incrementAndGet() + " in the waiting area");
      // atomic integer simplify the process of using mutexes and global variable
      // doing waiting_area_atomic_int.incrementAndGet saves us from having to do this
      // waiting_area_lock.lock();
      // print(++waiting_area_lock);
      // waiting_area_lock.unlock;
      // The ++waiting_area_lock command in assembly is 3 command
      // first load the value into the register
      // second increment the value
      // last read the value
      // during any of these 3 steps, another thread can change the value of waiting_area_lock
      // that's why we use mutex to prevent another thread from updating the value while we are reading it.

    } catch (InterruptedException ignore){}
  }
// Produce section
  private void getProduceSection() {
    try {
      produce_area.acquire();
      waiting_area.release();
      waiting_area_atomic_int.decrementAndGet();
      System.out.println("customer " + customer_id + " enters the produce section, there are " + produce_area_atomic_int.incrementAndGet() + " in the produce area");
      Thread.sleep(500 + random.nextInt(1001));
    } catch(InterruptedException ignore) {}
  }

// General Grocery Section
  private void getGeneralSection() {
    try {
      general_area.acquire();
      produce_area.release();
      produce_area_atomic_int.decrementAndGet();
      System.out.println("customer " + customer_id + " enters the general section, there are " + general_area_atomic_int.incrementAndGet() + " in the general area");
      Thread.sleep(1000 + random.nextInt(1001));
    } catch(InterruptedException ignore) {}
  }
// Frozen Section
  private void getFrozenSection() {
    try {
      frozen_area.acquire();
      general_area.release();
      general_area_atomic_int.decrementAndGet();
      System.out.println("customer " + customer_id + " enters the frozen section, there are " + frozen_area_atomic_int.incrementAndGet() + " in the frozen area");
      Thread.sleep(2500 + random.nextInt(1001));
    } catch(InterruptedException ignore) {}
  }

// Cashier Section
  private void getCashierSection(){
    try {
      cash_register.acquire();
      frozen_area.release();
      frozen_area_atomic_int.decrementAndGet();
      System.out.println("customer " + customer_id + " enters the cashier section, there are " + cash_register_atomic_int.incrementAndGet() + " people at the cash register");
      Thread.sleep(500 + random.nextInt(1001));
    } catch(InterruptedException ignore) {}
  }

  private void exitGroceryStore() {
    cash_register.release();
    cash_register_atomic_int.decrementAndGet();
    System.out.println("customer " + customer_id + " left the store");
  }

  @Override
  public void run() {
    getWaitingRoom();
    getProduceSection();
    getGeneralSection();
    getFrozenSection();
    getCashierSection();
    exitGroceryStore();
  }
}


