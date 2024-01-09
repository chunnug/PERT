package cxgMDS;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;

class Item implements Comparable<Item> {
 int ID;
 int price;
 List<Integer> description;
 public Item (int ID, int price, List<Integer> description)
 {
 this.ID = ID;
 this.price = price;
 //changes to the original list won't affect the description inside the Item
 //deep copying the list, we protect the internal state of the Item object.
 
 this.description = new LinkedList<>(description);
 } 
 public int getId() {
 return ID;
 }
 public void setId(int id) {
 this.ID = id;
 }
 public int getPrice() {
 return price;
 }
 public void setPrice(int price) {
 this.price = price;
 }
 public List<Integer> getDescription() {
 return description;
 }
 public void setDescription(List<Integer> description) {
 this.description = new LinkedList<>(description);
 }
 @Override
 public int compareTo(Item obj) {
 //order items from prices in ascending order
 int flag = this.price - obj.price; //if equal, change what we are comparing based of
 if (flag != 0)
 return flag;
 else
 return this.ID - obj.ID; //otherwise compare based on ID
 }
}

public class MDS {
Map <Integer, Item> pm;
Map <Integer, TreeSet<Item>> sm;
// Constructors
public MDS() {
 pm = new HashMap<>(); //primary mapping between ID and item
 sm = new HashMap<>(); //reverse mapping between index of Description and item
 }
 /* Public methods of MDS. Do not change their signatures.
 __________________________________________________________________
 a. Insert(id,price,list): insert a new item whose description is given
 in the list. If an entry with the same id already exists, then its
 description and price are replaced by the new values, unless list
 is null or empty, in which case, just the price is updated. 
 Returns 1 if the item is new, and 0 otherwise.
 */
public int insert(int id, int price, java.util.List<Integer> list) {
 int flag = 0; // flag to determine if item is new or old
 if (pm.containsKey(id)) { // if the item exists
 Item tmp = pm.get(id);
 if (list == null || list.isEmpty()) { // if list is null or empty
 tmp.setPrice(price); // only update the price

 return flag; // return 0 as it's not a new item
 }
 
 // If the list is not null or empty, then iterate through the old item's description and remove it from secondary mapping
 if (tmp.getDescription() != null) {
 for (int i : tmp.getDescription()) {
 if (sm.containsKey(i) && sm.get(i) != null) {
 sm.get(i).remove(tmp);
 }
 }
 }
 } 
 else {
 flag = 1; // the item is new
 }
 Item it = new Item(id, price, list); // create a new item with the given info
 pm.put(id, it); // put the new item in primary mapping
 // insert the new item into the secondary mapping
 if (list != null) {
 for (int i : list) {
 if (!sm.containsKey(i)) {
 sm.put(i, new TreeSet<>());
 }
 sm.get(i).add(it);
 }
 }

 return flag;
}
// b. Find(id): return price of item with given id (or 0, if not found).
public int find(int id) {
 Item item = pm.get(id);
 if (item == null)
 return 0;
 else
 
 return item.getPrice();
 
 }
/* 
 c. Delete(id): delete item from storage. Returns the sum of the
 ints that are in the description of the item deleted,
 or 0, if such an id did not exist.
 */
public int delete(int id) {
 int sum = 0;
 Item item = pm.remove(id);
 if (item != null) { 
 
 for (int i: item.getDescription()) {

 sum += i;
 TreeSet<Item> set = sm.get(i);
 if (set.size() > 1) { // Check if set is not null
 set.remove(item);
 
 }
 else
 sm.remove(i);
 
 }
 
 return sum;
 } else {
 return 0;
 }
}
/* 
 d. FindMinPrice(n): given an integer, find items whose description
 contains that number (exact match with one of the ints in the
 item's description), and return lowest price of those items.
 Return 0 if there is no such item.
 */
public int findMinPrice(int n) {
 TreeSet<Item> items = sm.get(n); //this returns the treeset of items from an 'n' index
 
 // check if the set is null or empty
 if (items == null || items.isEmpty()) {
 return 0;
 }


 return items.first().getPrice(); //we arrange our items based on price, in ascending order meaning at the top should be our minprice
 }
/* 
 e. FindMaxPrice(n): given an integer, find items whose description
 contains that number, and return highest price of those items.
 Return 0 if there is no such item.
 */
public int findMaxPrice(int n) {
 TreeSet<Item> items = sm.get(n); //this returns the treeset of items from an 'n' index
 
 // check if the set is null or empty
 if (items == null || items.isEmpty()) {
 return 0;
 }

 return items.last().getPrice(); //we arrange our items based on price, in ascending order meaning at the bottom should be our maxprice
 }
/* 
 f. FindPriceRange(n,low,high): given int n, find the number
 of items whose description contains n, and in addition,
 their prices fall within the given range, [low, high].
 */
public int findPriceRange(int n, int low, int high) {
 TreeSet<Item> items = sm.get(n); // this returns the treeset of items from an 'n' index
 int count = 0;
 if (items == null) { // If no such description index exists
 return 0;
 }

 for (Item i : items) {
 
 if (i.price >= low && i.price <= high) {
 count++;
 }
 }
 return count;
}
/*
 g. RemoveNames(id, list): Remove elements of list from the description of id.
 It is possible that some of the items in the list are not in the
 id's description. Return the sum of the numbers that are actually
 deleted from the description of id. Return 0 if there is no such id.
 */
public int removeNames(int id, java.util.List<Integer> list) {
 Item item = pm.get(id);
 if (item == null)
 return 0;
 List<Integer> Description = item.getDescription();
 int sum = 0;
 for (int i : list) {
 if (Description.contains(i)) {
 
 
 
 Description.remove(Integer.valueOf(i)); 
 sum += i;
 TreeSet<Item> setOfItems = sm.get(Integer.valueOf(i)); // this returns the treeset of items from an 'n' index
 if (setOfItems.size() > 1)
 setOfItems.remove(item);
 else
 sm.remove(Integer.valueOf(i));
 
 }}
item.setDescription(Description);
 return sum;
}
 
 }
 
 
