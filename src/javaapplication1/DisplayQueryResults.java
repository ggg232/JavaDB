// Fig. 24.28: DisplayQueryResults.java
// Display the contents of the Authors table in the books database.
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Container;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.sql.SQLException;
import java.util.regex.PatternSyntaxException;
import javax.swing.JFrame;
import javax.swing.JTextArea;
import javax.swing.JScrollPane;
import javax.swing.ScrollPaneConstants;
import javax.swing.JTable;
import javax.swing.JOptionPane;
import javax.swing.JButton;
import javax.swing.Box;
import javax.swing.JLabel;
import javax.swing.JTextField;
import javax.swing.RowFilter;
import javax.swing.table.TableRowSorter;
import javax.swing.table.TableModel;

public class DisplayQueryResults extends JFrame 
{
   // database URL, username and password
   private static final String DATABASE_URL = "jdbc:derby://localhost:1527/book";
   private static final String USERNAME = "book";
   private static final String PASSWORD = "book";
   
   // default query retrieves all data from authors table
   private static final String DEFAULT_QUERY = "SELECT * FROM authors",
           DEFAULT_QUERY2 = "SELECT * FROM titles";
   
   private static ResultSetTableModel tableModel,tableModel2;

   public static void main(String args[]) 
   {   
      // create ResultSetTableModel and display database table
      try 
      {
         // create TableModel for results of query SELECT * FROM authors
         tableModel = new ResultSetTableModel(
            DATABASE_URL, USERNAME, PASSWORD, DEFAULT_QUERY);
         tableModel2 = new ResultSetTableModel(
            DATABASE_URL, USERNAME, PASSWORD, DEFAULT_QUERY2);

         // set up JTextArea in which user types queries
         final JTextArea queryArea = new JTextArea(DEFAULT_QUERY, 3, 100);
         queryArea.setWrapStyleWord(true);
         queryArea.setLineWrap(true);
         final JTextArea queryArea2 = new JTextArea(DEFAULT_QUERY2, 3, 100);
         queryArea2.setWrapStyleWord(true);
         queryArea2.setLineWrap(true);
         
         JScrollPane scrollPane = new JScrollPane(queryArea,
            ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED, 
            ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
         JScrollPane scrollPane2 = new JScrollPane(queryArea2,
            ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED, 
            ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
         
         // set up JButton for submitting queries
         JButton submitButton = new JButton("Submit Query");
         JButton submitButton2 = new JButton("Submit Query");

         // create Box to manage placement of queryArea and 
         // submitButton in GUI
         Box boxNorth = Box.createHorizontalBox();
         boxNorth.add(scrollPane);
         boxNorth.add(submitButton);
         Box boxNorth2 = Box.createHorizontalBox();
         boxNorth2.add(scrollPane2);
         boxNorth2.add(submitButton2);

         // create JTable based on the tableModel
         JTable resultTable = new JTable(tableModel);
         JTable resultTable2 = new JTable(tableModel2);
         resultTable2.setBackground(Color.YELLOW);
         
         JLabel filterLabel = new JLabel("Filter:");
         final JTextField filterText = new JTextField();
         JButton filterButton = new JButton("Apply Filter");
         Box boxSouth = Box.createHorizontalBox();
         JLabel filterLabel2 = new JLabel("Filter:");
         final JTextField filterText2 = new JTextField();
         JButton filterButton2 = new JButton("Apply Filter");
         Box boxSouth2 = Box.createHorizontalBox();
         
         boxSouth.add(filterLabel);
         boxSouth.add(filterText);
         boxSouth.add(filterButton);
         boxSouth2.add(filterLabel2);
         boxSouth2.add(filterText2);
         boxSouth2.add(filterButton2);
         
         // place GUI components on JFrame's content pane
         JFrame window = new JFrame("Displaying Query Results");
         window.add(boxNorth, BorderLayout.NORTH);
         window.add(new JScrollPane(resultTable), BorderLayout.CENTER);
         window.add(boxSouth, BorderLayout.SOUTH);
         JFrame window2 = new JFrame("Displaying Query Results2");
         window2.add(boxNorth2, BorderLayout.NORTH);
         window2.add(new JScrollPane(resultTable2), BorderLayout.CENTER);
         window2.add(boxSouth2, BorderLayout.SOUTH);

         // create event listener for submitButton
         submitButton.addActionListener(        
            new ActionListener() 
            {
               public void actionPerformed(ActionEvent event)
               {
                  // perform a new query
                  try 
                  {
                     tableModel.setQuery(queryArea.getText());
                  }
                  catch (SQLException sqlException) 
                  {
                     JOptionPane.showMessageDialog(null, 
                        sqlException.getMessage(), "Database error", 
                        JOptionPane.ERROR_MESSAGE);
                     
                     // try to recover from invalid user query 
                     // by executing default query
                     try 
                     {
                        tableModel.setQuery(DEFAULT_QUERY);
                        queryArea.setText(DEFAULT_QUERY);
                     } 
                     catch (SQLException sqlException2) 
                     {
                        JOptionPane.showMessageDialog(null, 
                           sqlException2.getMessage(), "Database error", 
                           JOptionPane.ERROR_MESSAGE);
         
                        // ensure database connection is closed
                        tableModel.disconnectFromDatabase();
         
                        System.exit(1); // terminate application
                     }                 
                  } 
               } 
            }         
         ); // end call to addActionListener
         submitButton2.addActionListener(        
            new ActionListener() 
            {
               public void actionPerformed(ActionEvent event)
               {
                  // perform a new query
                  try 
                  {
                     tableModel2.setQuery(queryArea2.getText());
                  }
                  catch (SQLException sqlException) 
                  {
                     JOptionPane.showMessageDialog(null, 
                        sqlException.getMessage(), "Database error", 
                        JOptionPane.ERROR_MESSAGE);
                     
                     // try to recover from invalid user query 
                     // by executing default query
                     try 
                     {
                        tableModel2.setQuery(DEFAULT_QUERY2);
                        queryArea2.setText(DEFAULT_QUERY2);
                     } 
                     catch (SQLException sqlException2) 
                     {
                        JOptionPane.showMessageDialog(null, 
                           sqlException2.getMessage(), "Database error", 
                           JOptionPane.ERROR_MESSAGE);
         
                        // ensure database connection is closed
                        tableModel2.disconnectFromDatabase();
         
                        System.exit(1); // terminate application
                     }                 
                  } 
               } 
            }         
         ); // end call to addActionListener
         
         final TableRowSorter<TableModel> sorter = 
            new TableRowSorter<TableModel>(tableModel);
         resultTable.setRowSorter(sorter);
          final TableRowSorter<TableModel> sorter2 = 
            new TableRowSorter<TableModel>(tableModel2);
         resultTable2.setRowSorter(sorter2);
         
         // create listener for filterButton
         filterButton.addActionListener(           
            new ActionListener() 
            {
               // pass filter text to listener
               public void actionPerformed(ActionEvent e) 
               {
                  String text = filterText.getText();

                  if (text.length() == 0)
                     sorter.setRowFilter(null);
                  else
                  {
                     try
                     {
                        sorter.setRowFilter(
                           RowFilter.regexFilter(text));
                     } 
                     catch (PatternSyntaxException pse) 
                     {
                        JOptionPane.showMessageDialog(null,
                           "Bad regex pattern", "Bad regex pattern",
                           JOptionPane.ERROR_MESSAGE);
                     }
                  } 
               } 
            } 
         ); // end call to addActionLister
         // create listener for filterButton
         filterButton2.addActionListener(           
            new ActionListener() 
            {
               // pass filter text to listener
               public void actionPerformed(ActionEvent e) 
               {
                  String text = filterText2.getText();

                  if (text.length() == 0)
                     sorter2.setRowFilter(null);
                  else
                  {
                     try
                     {
                        sorter2.setRowFilter(
                           RowFilter.regexFilter(text));
                     } 
                     catch (PatternSyntaxException pse) 
                     {
                        JOptionPane.showMessageDialog(null,
                           "Bad regex pattern", "Bad regex pattern",
                           JOptionPane.ERROR_MESSAGE);
                     }
                  } 
               } 
            } 
         ); // end call to addActionLister

         // dispose of window when user quits application (this overrides
         // the default of HIDE_ON_CLOSE)
        // window.setDefaultCloseOperation(EXIT_ON_CLOSE);
         window.setSize(500, 250); 
         window.setVisible(true);
        // window2.setDefaultCloseOperation(EXIT_ON_CLOSE);
         Container ct = window2.getContentPane();
         ct.setBackground(Color.green);
         window2.setSize(500, 250);
         window2.setLocation(500, 0);
         window2.setVisible(true); 
          
         // ensure database is closed when user quits application
         window.addWindowListener(
            new WindowAdapter() 
            {
               public void windowClosed(WindowEvent event)
               {
                  tableModel.disconnectFromDatabase();
                  System.exit(0);
               } 
            } 
         ); 
          window2.addWindowListener(
            new WindowAdapter() 
            {
               public void windowClosed(WindowEvent event)
               {
                  tableModel2.disconnectFromDatabase();
                  System.exit(0);
               } 
            } 
         ); 
      } 
      catch (SQLException sqlException) 
      {
         JOptionPane.showMessageDialog(null, sqlException.getMessage(), 
            "Database error", JOptionPane.ERROR_MESSAGE);
         tableModel.disconnectFromDatabase();
         tableModel2.disconnectFromDatabase();
         System.exit(1); // terminate application
      }     
   } 
} // end class DisplayQueryResults



/**************************************************************************
 * (C) Copyright 1992-2014  by Deitel & Associates, Inc. and               *
 * Pearson Education, Inc. All Rights Reserved.                           *
 *                                                                        *
 * DISCLAIMER: The authors and publisher of this book have used their     *
 * best efforts in preparing the book. These efforts include the          *
 * development, research, and testing of the theories and programs        *
 * to determine their effectiveness. The authors and publisher make       *
 * no warranty of any kind, expressed or implied, with regard to these    *
 * programs or to the documentation contained in these books. The authors *
 * and publisher shall not be liable in any event for incidental or       *
 * consequential damages in connection with, or arising out of, the       *
 * furnishing, performance, or use of these programs.                     *
 *************************************************************************/
