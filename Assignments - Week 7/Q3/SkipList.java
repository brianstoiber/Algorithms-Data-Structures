import java.util.*;

public class SkipList {
	public SkipListEntry head; // First element of the top level
	public SkipListEntry tail; // Last element of the top level

	public int n; 				// number of entries in the Skip list

	public int h; 				// Height
	public Random r; 			// Coin toss

	/* ----------------------------------------------
    Constructor: empty skiplist

                         null        null
                          ^           ^
                          |           |
    head --->  null <-- -inf <----> +inf --> null
                          |           |
                          v           v
                         null        null
    ---------------------------------------------- */
	
	public SkipList() // Default constructor...
	{
		SkipListEntry p1, p2;

		p1 = new SkipListEntry(SkipListEntry.negInf, null);
		p2 = new SkipListEntry(SkipListEntry.posInf, null);

		head = p1;
		tail = p2;

		p1.right = p2;
		p2.left = p1;

		n = 0;
		h = 0;

		r = new Random();
	}

	/** Returns the number of entries in the hash table. */
	public int size() {
		return n;
	}

	/** Returns whether or not the table is empty. */
	public boolean isEmpty() {
		return (n == 0);
	}

	/* ------------------------------------------------------ 
	 * findEntry(k): find
	 * the largest key x <= k on the LOWEST level of the Skip List
	 * ------------------------------------------------------
	 */
	
	public SkipListEntry findEntry(String k) {
		SkipListEntry p;

		/*
		 * ----------------- Start at "head" -----------------
		 */
		p = head;

		while (true) {
			/* --------------------------------------------
			   Search RIGHT until you find a LARGER entry

		           E.g.: k = 34

		                     10 ---> 20 ---> 30 ---> 40
		                                      ^
		                                      |
		                                      p stops here
				p.right.key = 40
			   -------------------------------------------- */
			
			//  p.right can be null at first if p.right != null isn't used
			//  check here, NPE will be thrown for operations on p.right.key
			while (p.right != null && p.right.key != SkipListEntry.posInf && p.right.key.compareTo(k) <= 0) {
				p = p.right;
			}

			/* ---------------------------------
			   Go down one level if you can...
			   --------------------------------- */
			if (p.down != null) {
				p = p.down;
			} else
				break; // Lowest level reached... Exit...
		}

		return (p); // p.key <= k
	}

	/** Returns the value associated with a key. */
	public Integer get(String k) {
		SkipListEntry p;

		p = findEntry(k);

		if (k.equals(p.getKey()))
			return (p.value);
		else
			return (null);
	}

	 /* ------------------------------------------------------------------
    insertAfterAbove(p, q, y=(k,v) )

       1. create new entry (k,v)
	2. insert (k,v) AFTER p
	3. insert (k,v) ABOVE q

            p <--> (k,v) <--> p.right
                     ^
		      |
		      v
		      q

     Returns the reference of the newly created (k,v) entry
    ------------------------------------------------------------------ */
	public SkipListEntry insertAfterAbove(SkipListEntry p, SkipListEntry q, String k) {
		SkipListEntry e;

		e = new SkipListEntry(k, null);

		/* ---------------------------------------
		Use the links before they are changed !
		*
		*Here, k,v is e
		* 
		*  p <-> k,v
		*         |
		*         q
		*/
		e.left = p;
		e.right = p.right;
		e.down = q;

		 /* ---------------------------------------
		Now update the existing links..
		--------------------------------------- */
		p.right.left = e;
		p.right = e;
		q.up = e;

		return (e);
	}

	/** Put a key-value pair in the map, replacing previous one if it exists. */
	 
	/* 
	 * @param k- The key
	 * @param v - value v
	 * @return - old value if an entry is already there. otherwise null.	  
	 */
	public Integer put(String k, Integer v) {
		SkipListEntry p, q;
		int i;

		/*
		 * Attempt to find  if the key already exists in the list.
		 */
		p = findEntry(k);

		/*
		 * If key is present, replace the old value with the Integer v and
		 * return the old value.
		 */
		if (k.equals(p.getKey())) {
			Integer old = p.value;

			p.value = v;

			return (old);
		}

		/* ------------------------
		Insert new entry (k,v)
		------------------------ */

	     /* ------------------------------------------------------
		Link at the lowest level
		------------------------------------------------------ */

		q = new SkipListEntry(k, v);
		q.left = p;
		q.right = p.right;
		if (p.right != null)
			p.right.left = q; // added check here as it was causing NPE
		p.right = q;

		i = 0; // Current level = 0

		while (r.nextDouble() < 0.5) {
			// Coin flip success: make one more level....
			// System.out.println("i = " + i + ", h = " + h );

			/* ---------------------------------------------
			   Check if height exceed current height.
		 	   If so, make a new EMPTY level
			   --------------------------------------------- */
			
			if (i >= h) {
				SkipListEntry p1, p2;

				h = h + 1;

				p1 = new SkipListEntry(SkipListEntry.negInf, null);
				p2 = new SkipListEntry(SkipListEntry.posInf, null);

				p1.right = p2;
				p1.down = head;

				p2.left = p1;
				p2.down = tail;

				head.up = p1;
				tail.up = p2;

				head = p1;
				tail = p2;
			}

			/* -------------------------
			   Scan backwards...
			   ------------------------- */
			while (p.up == null) {
				p = p.left;
			}

			p = p.up;

			/* ---------------------------------------------
	           Add one more (k,v) to the column
		   --------------------------------------------- */
			SkipListEntry e;

			e = new SkipListEntry(k, null); // Don't need the value...

			/* ---------------------------------------
		   	   Initialize links of e
		   	   --------------------------------------- */
			e.left = p;
			e.right = p.right;
			e.down = q;

			/* ---------------------------------------
		   	   Change the neighboring links..
		   	   --------------------------------------- */
			if (p.right != null)
				p.right.left = e; 
			p.right = e;
			q.up = e;

			q = e; // Set q up for the next iteration

			i = i + 1; // Current level increased by 1

		}

		n = n + 1; // number of elements in skiplist incremented by 1

		return (null); // No old value
	}

	/** Removes the key-value pair with a specified key. */
	public Integer remove(String key) {
		return (null);
	}

	/**
	 * prints each horizontal level in skiplist in horizontal manner
	 */
	public void printHorizontal() {
		String s = "";
		int i;

		SkipListEntry p;

		 /* ----------------------------------
		Record the position of each entry
		---------------------------------- */
		p = head;

		while (p.down != null) {
			p = p.down;
		}

		i = 0;
		while (p != null) {
			p.pos = i++;
			p = p.right;
		}

		/*
		 * ------------------- Print... -------------------
		 */
		p = head;

		while (p != null) {
			s = getOneRow(p);
			System.out.println(s);

			p = p.down;
		}
	}

	/*
	 * 
	 * @param p the entry in question.
	 * @return string representing the entries on the right side of p in the lowest level.
	 * If p is not found, it prints the element that occurs immediately to left of p
	 * followed by entries in right side if p were to be inserted into the list.
	 */
	public String getOneRow(SkipListEntry p) {
		String s;
		int a, b, i;

		a = 0;

		s = "" + p.key;
		p = p.right;

		while (p != null) {
			SkipListEntry q;

			q = p;
			while (q.down != null)
				q = q.down;
			b = q.pos;

			s = s + " <-";

			for (i = a + 1; i < b; i++)
				s = s + "--------";

			s = s + "> " + p.key;

			a = b;

			p = p.right;
		}

		return (s);
	}

	/*
	 * prints each level in vertical manner. it is a 90 degree rotation of printhorizontal result.
	 */
	public void printVertical() {
		String s = "";

		SkipListEntry p;

		p = head;

		while (p.down != null)
			p = p.down;

		while (p != null) {
			s = getOneColumn(p);
			System.out.println(s);

			p = p.right;
		}
	}

	/*
	 * @param p
	 * @return the column corresponding to p. This is a subset of what you get for printhorizontal in one vertical line.
	 */

	public String getOneColumn(SkipListEntry p) {
		String s = "";

		while (p != null) {
			s = s + " " + p.key;

			p = p.up;
		}

		return (s);
	}

}