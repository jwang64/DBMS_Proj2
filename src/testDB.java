/**
 * Integration test for project 1
 * @Author James Wang
 */

import static java.lang.System.out;

class testDB
{
    public static void main(String[] args)
    {
	// create the tables
	out.println();
	Table jamesBooks = new Table ("James Books", "Title Author", "String String", "Title");
	Table peterBooks = new Table ("Peter Books", "Title Author", "String String", "Title");

	// create the books
	Comparable [] book1 = { "Twilight" , "Stephenie Meyer"};
	Comparable [] book2 = { "Harry Potter", "J.K. Rowling"};
	Comparable [] book3 = { "Hunger Games" , "Suzanne Collins"};
	Comparable [] book4 = { "Maze Runner", "James Dashner"};
	Comparable [] book5 = { "Dictionary" , "Merriam Webster"};
	// insert books into tables
	jamesBooks.insert(book1);
	jamesBooks.insert(book2);
	jamesBooks.insert(book3);
	jamesBooks.insert(book4);
	out.println();
	jamesBooks.print();

	peterBooks.insert(book3);
	peterBooks.insert(book4);
	peterBooks.insert(book5);
	out.println();
	peterBooks.print();

	// test out project
	out.println();
	Table t_project = jamesBooks.project("Author");
	t_project.print();

	// test out select w/ key
	out.println();
	Table t_select = jamesBooks.select(new KeyType ("Harry Potter"));
	t_select.print();

	// test union 
	out.println();
	Table t_union = jamesBooks.union(peterBooks);
	t_union.print();

	// test minus
	out.println ();
        Table t_minus = jamesBooks.minus (peterBooks);
        t_minus.print ();
	
	// test join
	out.println();
	Table t_join = jamesBooks.join(peterBooks);
	t_join.print();
	
    }

}