/*
 * BondCalculatorGui.java
 *
 * Created on January 18, 2011, 12:39 PM
 */

package bondcalculator;

//import java.io.*;
import java.util.Vector;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import java.util.Date;
import java.util.Calendar;
import java.util.GregorianCalendar;

//import com.foley.utility.ApplicationFrame;
import org.jfree.chart.ChartPanel;

/**
 *
 * @author Don
 */
public class BondCalculatorGui {

    class ResetBtnCallback implements ActionListener {
        public void actionPerformed(ActionEvent event) {
            bond.reset();
            reset();
            updateDataComponents();
        }
    }
    
    class CalculateBtnCallback implements ActionListener {
        private boolean getUserData () {
            try {
                double amount = Double.parseDouble(txtPrincipal.getText());
                int nbrMonths = Integer.parseInt(txtTerm.getText());
                double installment = Double.parseDouble(txtInstalment.getText());
                int installmentDom = Integer.parseInt(txtInstalmentDom.getText());  // 1-based day
                double primeDelta = 0.0, fixedRate = 0.0;
                if (rateOptions[0].isSelected()) {
                    // prime +/-
                    primeDelta = Double.parseDouble(txtRateSetting.getText());
                } else {
                    // fixed
                    fixedRate = Double.parseDouble(txtRateSetting.getText());
                    if (fixedRate == 0.0) {
                        txtRateSetting.setText("???");
                        return false;
                    }
                }
                bond.setFixedRate(fixedRate);  // force to remove ambiguity
                
                Date date = BondUtils.stringToDate(txtStartDate.getText(), null);
                GregorianCalendar calnStart = new GregorianCalendar();
                calnStart.set(date.getYear()+1900, date.getMonth(), date.getDate());
                double initFee = Double.parseDouble(txtInitFee.getText());
                double montlhyFee = Double.parseDouble(txtMonthlyFee.getText());
                bond.setValues(amount, nbrMonths, calnStart, installment, installmentDom, fixedRate, primeDelta);
                bond.setFees(initFee, montlhyFee);
            }
            catch (Exception x) {
                return false;
            }            
            return true;
        }
        
        public void actionPerformed(ActionEvent event) {
            if (getUserData()) {
                updateDataComponents();
            }
            else {
                JOptionPane.showMessageDialog(null, "Invalid data specified!");
            }
        }
    }
    
    class RateOptionCallback implements ActionListener {
        public void actionPerformed(ActionEvent event) {
            if (rateOptions[0].isSelected()) {
                lblRateSetting.setText("  +/-");
                txtRateSetting.setText(String.format("%+.2f", (float)bond.getPrimeDeltaRate()));
            }
            else {
                lblRateSetting.setText("    %");
                txtRateSetting.setText(String.format("%4.2f", (float)bond.getFixedRate()));
            }
        }
    }
    
    class DayMonthOptionCallback implements ActionListener {
        public void actionPerformed(ActionEvent event) {
            genDataTable();
        }
    }    
    
    class AdhocOptionCallback implements ActionListener {
        public void actionPerformed(ActionEvent event) {
            bond.setAdhocState(adhocOptions[0].isSelected());
            updateDataComponents();
        }
    }

    class InstalmentRefreshBtnCallback  implements ActionListener {
    	public void actionPerformed(ActionEvent event) {
    		bond.refreshInstalments();
    		updateDataComponents();
    	}
    }
    
    class AdhocRefreshBtnCallback  implements ActionListener {
    	public void actionPerformed(ActionEvent event) {
    		bond.refreshAdhocs();
    		updateDataComponents();
    	}
    }
    
    class ChartOptionCallback implements ActionListener {
        public void actionPerformed(ActionEvent event) {
            chartType = chartOptions[0].isSelected() ?
            				BondChart.ChartType.PERCENT : BondChart.ChartType.VALUE;
            genGraph();
        }
    }    

    class PaymentListItemCallback implements ListSelectionListener {

		@Override
		public void valueChanged(ListSelectionEvent item) {
			if (! item.getValueIsAdjusting()) {
				Payment payment = bond.getInstalments()[lstInstalment.getSelectedIndex()];
				System.out.println(payment.toString());
			}			
		}    	
    }
    
    private Bond bond;
    
    private BondChart.ChartType chartType = BondChart.ChartType.PERCENT;
    
    private JFrame frame;
    // Component Declaration
    
    /** Main tab **/
    private JPanel pnlMain;
    private JPanel pnlDetail;
    private JPanel pnlTable;    
    
    private JTextField txtBondName;
    private JTextField txtPrincipal;
    private JTextField txtTerm;
    private JTextField txtStartDate;
    private JTextField txtInstalment;
    private JTextField txtInstalmentDom;
    private JTextField txtInitFee;
    private JTextField txtMonthlyFee;
    private JTextField txtRateSetting;
    private JTextField txtTotalPaid;
    private JTextField txtTotalInterest;
    
    private JLabel lblRateSetting;
    
    JList lstRate;
    JList lstInstalment;
    JList lstAdhoc;
    
    private JButton btnReset;
    private JButton btnCalculate;
    
    private JRadioButton[] rateOptions;
    private JRadioButton[] options;
    private JRadioButton[] adhocOptions;
    private JRadioButton[] chartOptions;
    
    private JTable tblGrid;
    
    /** Rate tab **/
    private JPanel pnlRate;
    
    /** Payment tab **/
    private JPanel pnlPayment;
    
    /** Graph tab **/
    private JPanel pnlGraph;
    
    private void initGui() {
        //frame = new ApplicationFrame( "Don's Bond Buster" );
        frame = new JFrame("Bond Buster");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        
        JTabbedPane tabs = new JTabbedPane();
        
        /** Main tab **/
        // create the panels
        pnlMain = new JPanel();
        JPanel pnlTop = new JPanel();
        pnlDetail = new JPanel();
        pnlTable = new JPanel();            
        JPanel pnlControls = new JPanel();
        JPanel pnlButtons = new JPanel();
        JPanel pnlOptions = new JPanel();
        JPanel pnlSummary = new JPanel();
        
        // set the layouts for panels
        pnlMain.setLayout(new BorderLayout());        
        pnlTop.setLayout(new GridLayout(1,2));
        pnlControls.setLayout(new GridLayout(3,2));
        
        // Bond detail components
        GridBagLayout gb = new GridBagLayout();        
        
        JLabel lblName = new JLabel("Name:  ");
        lblName.setFont(new Font("Dialog",Font.BOLD,12)); 
        //lblName.setHorizontalAlignment(JLabel.LEFT);
        GridBagConstraints gc = new GridBagConstraints();
        gc.gridx = gc.gridy = 0;
        gc.gridwidth = 1;
        gc.gridheight = 1;
        gc.weightx = 1;
        gc.weighty = 100;
        gc.fill = GridBagConstraints.NONE;
        gc.anchor = GridBagConstraints.EAST;
        gb.setConstraints(lblName, gc);
        pnlDetail.setLayout(gb);
                
        pnlDetail.setBorder(BorderFactory.createLoweredBevelBorder());
        
        txtBondName = new JTextField("");
        txtBondName.setForeground(Color.black);
        txtBondName.setBackground(Color.yellow);
        txtBondName.setFont(new Font("Dialog",Font.BOLD,12));
        gc = new GridBagConstraints();
        gc.gridx = 1;
        gc.gridy = 0;
        gc.gridwidth = 10;
        gc.gridheight = 1;
        gc.weightx = 100;
        gc.weighty = 100;
        gc.fill = GridBagConstraints.HORIZONTAL;
        gc.anchor = GridBagConstraints.WEST;
        gb.setConstraints(txtBondName, gc);

        JLabel lblPrincipal = new JLabel("Principal:  ");
        lblPrincipal.setFont(new Font("Dialog",Font.BOLD,12));        
        gc = new GridBagConstraints();
        gc.gridx = 0;
        gc.gridy = 1;
        gc.gridwidth = 1;
        gc.gridheight = 1;
        gc.weightx = 10;
        gc.weighty = 100;
        gc.fill = GridBagConstraints.NONE;
        gc.anchor = GridBagConstraints.EAST;
        gb.setConstraints(lblPrincipal, gc);

        txtPrincipal = new JTextField("");
        txtPrincipal.setForeground(Color.black);
        txtPrincipal.setBackground(Color.yellow);
        txtPrincipal.setFont(new Font("Dialog",Font.BOLD,12));
        gc = new GridBagConstraints();
        gc.gridx = 1;
        gc.gridy = 1;
        gc.gridwidth = 10;
        gc.gridheight = 1;
        gc.weightx = 100;
        gc.weighty = 100;
        gc.fill = GridBagConstraints.HORIZONTAL;
        gc.anchor = GridBagConstraints.WEST;
        gb.setConstraints(txtPrincipal, gc);                
        
        JLabel lblTerm = new JLabel("Term (months):  ");
        lblTerm.setFont(new Font("Dialog",Font.BOLD,12));
        gc = new GridBagConstraints();
        gc.gridx = 0;
        gc.gridy = 2;
        gc.gridwidth = 1;
        gc.gridheight = 1;
        gc.weightx = 10;
        gc.weighty = 100;
        gc.fill = GridBagConstraints.NONE;
        gc.anchor = GridBagConstraints.EAST;
        gb.setConstraints(lblTerm, gc);
        
        txtTerm = new JTextField("");
        txtTerm.setForeground(Color.black);
        txtTerm.setBackground(Color.yellow);
        txtTerm.setFont(new Font("Dialog",Font.BOLD,12));
        gc = new GridBagConstraints();
        gc.gridx = 1;
        gc.gridy = 2;
        gc.gridwidth = 10;
        gc.gridheight = 1;
        gc.weightx = 100;
        gc.weighty = 100;
        gc.fill = GridBagConstraints.HORIZONTAL;
        gc.anchor = GridBagConstraints.WEST;
        gb.setConstraints(txtTerm, gc);                
        
        JLabel lblDate = new JLabel("Start Date:  ",Label.LEFT);
        lblDate.setFont(new Font("Dialog",Font.BOLD,12));
        gc = new GridBagConstraints();
        gc.gridx = 0;
        gc.gridy = 3;
        gc.gridwidth = 1;
        gc.gridheight = 1;
        gc.weightx = 10;
        gc.weighty = 100;
        gc.fill = GridBagConstraints.NONE;
        gc.anchor = GridBagConstraints.EAST;
        gb.setConstraints(lblDate, gc);
        
        txtStartDate = new JTextField("");
        txtStartDate.setForeground(Color.black);
        txtStartDate.setBackground(Color.yellow);
        txtStartDate.setFont(new Font("Dialog",Font.BOLD,12));
        gc = new GridBagConstraints();
        gc.gridx = 1;
        gc.gridy = 3;
        gc.gridwidth = 10;
        gc.gridheight = 1;
        gc.weightx = 100;
        gc.weighty = 100;
        gc.fill = GridBagConstraints.HORIZONTAL;
        gc.anchor = GridBagConstraints.WEST;
        gb.setConstraints(txtStartDate, gc);          
        
        //lblMonths = new JLabel("(months)",Label.LEFT);
        //lblMonths.setFont(new Font("Dialog",Font.BOLD,12));
        
        JLabel lblInstalment = new JLabel("Payment:  ",Label.LEFT);
        lblInstalment.setFont(new Font("Dialog",Font.BOLD,12));
        gc = new GridBagConstraints();
        gc.gridx = 0;
        gc.gridy = 4;
        gc.gridwidth = 1;
        gc.gridheight = 1;
        gc.weightx = 10;
        gc.weighty = 100;
        gc.fill = GridBagConstraints.NONE;
        gc.anchor = GridBagConstraints.EAST;
        gb.setConstraints(lblInstalment, gc);
        
        txtInstalment = new JTextField("");
        txtInstalment.setForeground(Color.black);
        txtInstalment.setBackground(Color.yellow);
        txtInstalment.setFont(new Font("Dialog",Font.BOLD,12));
        gc = new GridBagConstraints();
        gc.gridx = 1;
        gc.gridy = 4;
        gc.gridwidth = 10;
        gc.gridheight = 1;
        gc.weightx = 100;
        gc.weighty = 100;
        gc.fill = GridBagConstraints.HORIZONTAL;
        gc.anchor = GridBagConstraints.WEST;
        gb.setConstraints(txtInstalment, gc);        

        JLabel lblInstalmentDom = new JLabel("Payment Day:  ",Label.LEFT);
        lblInstalmentDom.setFont(new Font("Dialog",Font.BOLD,12));
        gc = new GridBagConstraints();
        gc.gridx = 0;
        gc.gridy = 5;
        gc.gridwidth = 1;
        gc.gridheight = 1;
        gc.weightx = 10;
        gc.weighty = 100;
        gc.fill = GridBagConstraints.NONE;
        gc.anchor = GridBagConstraints.EAST;
        gb.setConstraints(lblInstalmentDom, gc);
        
        txtInstalmentDom = new JTextField("");
        txtInstalmentDom.setForeground(Color.black);
        txtInstalmentDom.setBackground(Color.yellow);
        txtInstalmentDom.setFont(new Font("Dialog",Font.BOLD,12));
        gc = new GridBagConstraints();
        gc.gridx = 1;
        gc.gridy = 5;
        gc.gridwidth = 10;
        gc.gridheight = 1;
        gc.weightx = 100;
        gc.weighty = 100;
        gc.fill = GridBagConstraints.HORIZONTAL;
        gc.anchor = GridBagConstraints.WEST;
        gb.setConstraints(txtInstalmentDom, gc); 

        JLabel lblInitFee = new JLabel("Setup Fee:  ",Label.LEFT);
        lblInstalmentDom.setFont(new Font("Dialog",Font.BOLD,12));
        gc = new GridBagConstraints();
        gc.gridx = 0;
        gc.gridy = 6;
        gc.gridwidth = 1;
        gc.gridheight = 1;
        gc.weightx = 10;
        gc.weighty = 100;
        gc.fill = GridBagConstraints.NONE;
        gc.anchor = GridBagConstraints.EAST;
        gb.setConstraints(lblInitFee, gc);
        
        txtInitFee = new JTextField("");
        txtInitFee.setForeground(Color.black);
        txtInitFee.setBackground(Color.yellow);
        txtInitFee.setFont(new Font("Dialog",Font.BOLD,12));
        gc = new GridBagConstraints();
        gc.gridx = 1;
        gc.gridy = 6;
        gc.gridwidth = 10;
        gc.gridheight = 1;
        gc.weightx = 100;
        gc.weighty = 100;
        gc.fill = GridBagConstraints.HORIZONTAL;
        gc.anchor = GridBagConstraints.WEST;
        gb.setConstraints(txtInitFee, gc);

        JLabel lblMonthlyFee = new JLabel("Monthly Fee:  ",Label.LEFT);
        lblInstalmentDom.setFont(new Font("Dialog",Font.BOLD,12));
        gc = new GridBagConstraints();
        gc.gridx = 0;
        gc.gridy = 7;
        gc.gridwidth = 1;
        gc.gridheight = 1;
        gc.weightx = 10;
        gc.weighty = 100;
        gc.fill = GridBagConstraints.NONE;
        gc.anchor = GridBagConstraints.EAST;
        gb.setConstraints(lblMonthlyFee, gc);
        
        txtMonthlyFee = new JTextField("");
        txtMonthlyFee.setForeground(Color.black);
        txtMonthlyFee.setBackground(Color.yellow);
        txtMonthlyFee.setFont(new Font("Dialog",Font.BOLD,12));
        gc = new GridBagConstraints();
        gc.gridx = 1;
        gc.gridy = 7;
        gc.gridwidth = 10;
        gc.gridheight = 1;
        gc.weightx = 100;
        gc.weighty = 100;
        gc.fill = GridBagConstraints.HORIZONTAL;
        gc.anchor = GridBagConstraints.WEST;
        gb.setConstraints(txtMonthlyFee, gc);
        
        JPanel pnlRateEdit = new JPanel();
        JLabel lblRate = new JLabel("Rate:  ", Label.LEFT);
        lblInstalmentDom.setFont(new Font("Dialog",Font.BOLD,12));
        gc = new GridBagConstraints();
        gc.gridx = 0;
        gc.gridy = 8;
        gc.gridwidth = 1;
        gc.gridheight = 1;
        gc.weightx = 10;
        gc.weighty = 100;
        gc.fill = GridBagConstraints.NONE;
        gc.anchor = GridBagConstraints.EAST;
        gb.setConstraints(lblRate, gc);
        
        rateOptions = new JRadioButton[2];
        rateOptions[0] = new JRadioButton("Prime");
        rateOptions[1] = new JRadioButton("Fixed");
        rateOptions[0].setSelected(true);
        ButtonGroup rgroup = new ButtonGroup();
        for (int k = 0; k < rateOptions.length; ++k) {
            rateOptions[k].addActionListener(new RateOptionCallback());
            rgroup.add(rateOptions[k]);
            pnlRateEdit.add(rateOptions[k]);
        }
        lblRateSetting = new JLabel("  +/-",Label.LEFT);
        pnlRateEdit.add(lblRateSetting);
        txtRateSetting = new JTextField("      ");
        pnlRateEdit.add(txtRateSetting);
        
        gc = new GridBagConstraints();
        gc.gridx = 1;
        gc.gridy = 8;
        gc.gridwidth = 10;
        gc.gridheight = 1;
        gc.weightx = 100;
        gc.weighty = 100;
        gc.fill = GridBagConstraints.HORIZONTAL;
        gc.anchor = GridBagConstraints.WEST;
        gb.setConstraints(pnlRateEdit, gc);               
        
        pnlDetail.add(lblName);
        pnlDetail.add(txtBondName);
        pnlDetail.add(lblPrincipal);
        pnlDetail.add(txtPrincipal);
        pnlDetail.add(lblTerm);
        pnlDetail.add(txtTerm);
        pnlDetail.add(lblDate);
        pnlDetail.add(txtStartDate);
        pnlDetail.add(lblInstalment);
        pnlDetail.add(txtInstalment);
        pnlDetail.add(lblInstalmentDom);
        pnlDetail.add(txtInstalmentDom);
        pnlDetail.add(lblInitFee);
        pnlDetail.add(txtInitFee);
        pnlDetail.add(lblMonthlyFee);
        pnlDetail.add(txtMonthlyFee);
        pnlDetail.add(lblRate);
        pnlDetail.add(pnlRateEdit);
                        
        pnlTop.add(pnlDetail);
        
        //pnlControls.setLayout(new GridLayout(2,1));
        btnCalculate = new JButton("Calculate");
        btnCalculate.addActionListener(new CalculateBtnCallback());
        pnlButtons.add(btnCalculate);
        btnReset = new JButton("Reset");
        btnReset.addActionListener(new ResetBtnCallback());
        pnlButtons.add(btnReset);                
        pnlControls.add(pnlButtons);
       
        JLabel lblChoose = new JLabel("Figures by");
        pnlOptions.add(lblChoose);
        options = new JRadioButton[2];
        options[0] = new JRadioButton("Day");
        options[1] = new JRadioButton("Month");
        options[1].setSelected(true);
        ButtonGroup group = new ButtonGroup();
        for (int k = 0; k < options.length; ++k) {
            options[k].addActionListener(new DayMonthOptionCallback());
            group.add(options[k]);
            pnlOptions.add(options[k]);
        }
                
        GridBagLayout gb2 = new GridBagLayout();        
        
        JLabel lblTotalPaid = new JLabel("  Total Debt  ");
        lblTotalPaid.setFont(new Font("Dialog",Font.BOLD,12)); 
        gc = new GridBagConstraints();
        gc.gridx = gc.gridy = 0;
        //gc.gridwidth = 1;
        //gc.gridheight = 1;
        gc.weightx = 1;
        gc.weighty = 100;
        gc.fill = GridBagConstraints.NONE;
        gc.anchor = GridBagConstraints.EAST;
        gb2.setConstraints(lblTotalPaid, gc);
        pnlSummary.setLayout(gb2);        
                
        //pnlSummary.setBorder(BorderFactory.createLoweredBevelBorder());
        pnlSummary.setBorder(BorderFactory.createLineBorder(Color.red, 2));
        
        txtTotalPaid = new JTextField("");
        txtTotalPaid.setEditable(false);
        txtTotalPaid.setForeground(Color.black);
        txtTotalPaid.setBackground(Color.cyan);
        txtTotalPaid.setFont(new Font("Courier",Font.BOLD,13));  
        gc = new GridBagConstraints();
        gc.gridx = 1;
        gc.gridy = 0;
        //gc.gridwidth = 10;
        //gc.gridheight = 1;
        gc.weightx = 100;
        //gc.weighty = 100;
        gc.fill = GridBagConstraints.HORIZONTAL;
        //gc.anchor = GridBagConstraints.WEST;
        gb2.setConstraints(txtTotalPaid, gc);        

        JLabel lblTotalInterest = new JLabel("Interest ");
        lblTotalInterest.setFont(new Font("Dialog",Font.BOLD,12)); 
        gc = new GridBagConstraints();
        gc.gridx = 0;
        gc.gridy = 1;
        //gc.gridwidth = 1;
        //gc.gridheight = 1;
        gc.weightx = 1;
        //gc.weighty = 100;
        //gc.fill = GridBagConstraints.NONE;
        //gc.anchor = GridBagConstraints.EAST;
        gb2.setConstraints(lblTotalInterest, gc);
        
        txtTotalInterest = new JTextField("");
        txtTotalInterest.setEditable(false);
        txtTotalInterest.setForeground(Color.black);
        txtTotalInterest.setBackground(Color.orange);
        txtTotalInterest.setFont(new Font("Courier",Font.BOLD,13));
        gc = new GridBagConstraints();
        gc.gridx = 1;
        gc.gridy = 1;
        //gc.gridwidth = 10;
        //gc.gridheight = 1;
        gc.weightx = 100;
        //gc.weighty = 100;
        gc.fill = GridBagConstraints.HORIZONTAL;
        //gc.anchor = GridBagConstraints.WEST;
        gb2.setConstraints(txtTotalInterest, gc);
        
        pnlSummary.add(lblTotalPaid);
        pnlSummary.add(txtTotalPaid);
        pnlSummary.add(lblTotalInterest);
        pnlSummary.add(txtTotalInterest);        
        
        pnlControls.add(pnlOptions);
        pnlControls.add(pnlSummary);
        pnlTop.add(pnlControls);
        
        pnlMain.add(pnlTop, "North");
        //pnlMain.add(pnlOptions, "East");
        
        tabs.addTab("Main", pnlMain);
                
        /** Rates tab **/
        pnlRate = new JPanel();
        pnlRate.setLayout(new BorderLayout());        
        tabs.addTab("Rates", pnlRate);

        /** Payments tab **/
        pnlPayment = new JPanel();
        pnlPayment.setLayout(new GridLayout(2,1));        
        tabs.addTab("Payments", pnlPayment);
        
        /** Add tab pane to main frame **/
        frame.getContentPane().add(tabs);
        
        /** Graph tab **/
        pnlGraph = new JPanel();
        pnlGraph.setLayout(new BorderLayout());
        tabs.addTab("Graph", pnlGraph);

        updateDataComponents();
        
        //frame.pack();
        frame.setSize(600, 800);
    }
    
    private void updateDataComponents() {
        bond.calculate();
    	genDataTable();
    	genRates();
    	genPayments();
    	genGraph();
    }
    
    private void genRates() {
    	pnlRate.removeAll();    	
        pnlRate.add(new JLabel(" --- Prime Interest Rate ---"), "North");
        JPanel pnlRateEdit = new JPanel();  
        pnlRate.add(pnlRateEdit, "South");
        pnlRate.add(loadRatePanel(), "Center");    	
    }
    
    private void genPayments() {
    	pnlPayment.removeAll();
    	
        JPanel pnlInstalment = new JPanel();
        pnlInstalment.setLayout(new BorderLayout());
        JLabel lbl = new JLabel("Instalment Schedule");
        lbl.setForeground(Color.red);
        pnlInstalment.add(lbl , "North");
        pnlInstalment.add(loadInstalmentPanel(), "Center");
        JButton btnRefresh1 = new JButton("Refresh");
        btnRefresh1.addActionListener(new InstalmentRefreshBtnCallback());
        JPanel pnlDummy = new JPanel();
        pnlDummy.add(btnRefresh1);
        pnlInstalment.add(pnlDummy, "South");        
        pnlPayment.add(pnlInstalment);
        
        JPanel pnlAdhoc = new JPanel();
        pnlAdhoc.setLayout(new BorderLayout());
        lbl = new JLabel(" Ad-Hoc Payments");
        lbl.setForeground(Color.magenta);
        pnlAdhoc.add(lbl, "North");
        pnlAdhoc.add(loadAdhocPanel(), "Center");
        JPanel pnlOptions = new JPanel();
        adhocOptions = new JRadioButton[2];
        adhocOptions[0] = new JRadioButton("Enable");
        adhocOptions[1] = new JRadioButton("Disable");
        if (bond.getAdhocState()) {
        	adhocOptions[0].setSelected(true);
        } else {
        	adhocOptions[1].setSelected(true);
    	}
        ButtonGroup rgroup = new ButtonGroup();
        for (int k = 0; k < adhocOptions.length; ++k) {
        	adhocOptions[k].addActionListener(new AdhocOptionCallback());
            rgroup.add(adhocOptions[k]);
            pnlOptions.add(adhocOptions[k]);
        }
        JButton btnRefresh2 = new JButton("Refresh");
        btnRefresh2.addActionListener(new AdhocRefreshBtnCallback());
        pnlOptions.add(btnRefresh2);        
        pnlAdhoc.add(pnlOptions, "South");
        pnlPayment.add(pnlAdhoc);            	
    }
    
    private void genDataTable() {
        String[] columns = {"Row", "Date", "%Rate", "Interest", "Payment", "Balance"};
        double interestTotal = 0,
               paymentTotal = 0;
        boolean byDay = options[0].isSelected();
        Vector<String> vcol = new Vector<String>();
        for (int col = 0; col < columns.length; ++col) {
            vcol.addElement(columns[col]);
        }
        BondDay data[] = bond.getBondDates(byDay ? 'D' : 'M');
        Vector<Vector<String>> vbd = new Vector<Vector<String>>();
      
        GregorianCalendar cal = bond.getStartDateCalendar();
        for (int d = 0; d < data.length; ++d) {
            assert(data[d] != null);       
            double balance = data[d].getBalance();
            Vector<String> vRow = new Vector<String>();
            
            vRow.addElement("" + (d+1));  // Row#
            
            Date date = data[d].getDayDate();
            //vRow.addElement(DateFormat.getDateInstance().format(cal.getTime()));
            if (date == null) {
                date = cal.getTime();
                cal.add(Calendar.DATE, 1);
            }
            vRow.addElement(BondUtils.dateToString(date, byDay ? null : "yyyy-MM"));
            //vRow.addElement(data[d].getDayNumber());
            if (byDay) {
                double rate = bond.getFixedRate();
                if (rate == 0.0) {
                    rate = bond.getInterestRateForDate(date) + bond.getPrimeDeltaRate();
                }
                vRow.addElement(String.format("%.2f", (float)rate));
            }
            else vRow.addElement("");
            
            double interest = data[d].getInterest();
            interestTotal += interest;
            double payment = data[d].getPayment();            
            
            paymentTotal += payment;
            vRow.addElement(String.format("%.2f", (float)interest));
            vRow.addElement(String.format("%.2f", (float)payment));
            vRow.addElement(String.format("%.2f", (float)balance));
            vbd.add(vRow);
            
            if (balance < 1.0) {
                break;
            }            
        }
        
        // Totals row
        Vector<String> vRow = new Vector<String>();        
        vRow.addElement("");
        vRow.addElement("TOTALS");
        vRow.addElement("");
        vRow.addElement(String.format("%.2f", (float)interestTotal));
        vRow.addElement(String.format("%.2f", (float)paymentTotal));
        vbd.add(vRow);
        
        //DecimalFormat formatter = new DecimalFormat("###,###,###.##");
        
        String amount = String.format("%.2f", (float)(bond.getCapitalPaid() + bond.getFeesPaid()));
        amount = BondUtils.commaFormatted(amount, 0);
        int amtLen = amount.length();
        //txtTotalPaid.setText(String.format("%.2f", (float)(bond.getCapitalPaid() + bond.getFeesPaid())));
        txtTotalPaid.setText(amount);
        amount = String.format("%.2f", (float)bond.getInterestPaid());
        amount = BondUtils.commaFormatted(amount, amtLen);
        txtTotalInterest.setText(amount);        

        // clear previous table
        //tblGrid = null;
        pnlTable.removeAll();
        /* workaround bug? where scroll bars remain and get all messed up!
         * Unfortunately this causes the table to disappear on resize or next focus... */
        //pnlTable = new JPanel();
        tblGrid = new JTable(vbd, vcol);
        //tblGrid.setPreferredScrollableViewportSize(new Dimension(500, 500));
        //tblGrid.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
        tblGrid.getColumn("Row").setPreferredWidth(16);
        tblGrid.getColumn("Row").setHeaderValue((Object)"");
        if (! byDay) {
            tblGrid.removeColumn(tblGrid.getColumn("%Rate"));
        }
        //tblGrid.getColumn(tblGrid.getColumnCount()-1).setCellRenderer(new cellRenderer)

        JScrollPane scroll = new JScrollPane(tblGrid);
        scroll.validate();
        
        pnlTable.add(scroll);
        
        // add to main panel
        pnlMain.add(pnlTable, "Center");
        pnlMain.revalidate();
    }
    
    private void genGraph() {
        //ChartPanel chart = BondChart.createChart(BondChart.ChartType.PERCENT, bond);
    	ChartPanel chart = BondChart.createChart(chartType, bond);
        pnlGraph.removeAll();
        pnlGraph.add(chart, "Center");
        chartOptions = new JRadioButton[2];
        chartOptions[0] = new JRadioButton("Percent");
        chartOptions[1] = new JRadioButton("Amount");
        if (chartType == BondChart.ChartType.PERCENT) {
        	chartOptions[0].setSelected(true);
        } else {
        	chartOptions[1].setSelected(true);
    	}
        JPanel pnlOptions = new JPanel();
        ButtonGroup rgroup = new ButtonGroup();
        for (int k = 0; k < chartOptions.length; ++k) {
        	chartOptions[k].addActionListener(new ChartOptionCallback());
            rgroup.add(chartOptions[k]);
            pnlOptions.add(chartOptions[k]);
        }
        pnlGraph.add(pnlOptions, "South");        
        pnlGraph.revalidate();
    }    
    
    private JScrollPane loadRatePanel() {
        InterestRate[] rates = bond.getAllRates();
        String[] rateStr = new String[rates.length];
        
        for (int k = 0; k < rates.length; ++k) {
            rateStr[k] = BondUtils.dateToString(rates[k].getEffectiveDate(), null);
            rateStr[k] += "          ";
            rateStr[k] += ("" + rates[k].getInterestRate());
        }
        lstRate = new JList(rateStr);
        JScrollPane scroll = new JScrollPane(lstRate);
        return scroll;
    }
    
    private JScrollPane loadInstalmentPanel() {
    	Payment[] instalments = bond.getInstalments();
    	if (instalments == null) {
    		return null;
    	}
/*    	
    	String[] instalmentStr = new String[instalments.length];
    	
        for (int k = 0; k < instalments.length; ++k) {
        	instalmentStr[k] = DateUtils.dateToString(instalments[k].getEffectiveDate(), null);
        	instalmentStr[k] += "          ";
        	instalmentStr[k] += ("" + instalments[k].getPayment());
        }
        lstInstalment = new JList(instalmentStr);
*/        
    	lstInstalment = new JList(instalments);
        lstInstalment.addListSelectionListener(new PaymentListItemCallback());
        JScrollPane scroll = new JScrollPane(lstInstalment);
        return scroll;    	
    }
    
    private JScrollPane loadAdhocPanel() {
    	Payment[] adhoc = bond.getAdhocs();
    	if (adhoc == null) {
    		return null;
    	}
    	String[] adhocStr = new String[adhoc.length];
    	
        for (int k = 0; k < adhoc.length; ++k) {
        	adhocStr[k] = BondUtils.dateToString(adhoc[k].getEffectiveDate(), null);
        	adhocStr[k] += "          ";
        	adhocStr[k] += ("" + adhoc[k].getPayment());
        }
        lstAdhoc = new JList(adhocStr);
        JScrollPane scroll = new JScrollPane(lstAdhoc);
        return scroll;    	
    }
    
    public void reset() {
        txtBondName.setText(bond.getId());
        txtPrincipal.setText(String.format("%.2f", (float)bond.getPrincipal()));
        txtTerm.setText("" + bond.getTerm());
        //txtStartDate.setText(bond.getStartDate().toString());
        //txtStartDate.setText(DateFormat.getDateInstance().format(bond.getStartDate()));
        txtStartDate.setText(BondUtils.dateToString(bond.getStartDate(), null));
        txtInstalment.setText(String.format("%.2f", (float)bond.getBasicInstalment()));
        txtInstalmentDom.setText("" + bond.getInstalmentDayOfMonth());
        txtInitFee.setText(String.format("%.2f", (float)bond.getInitFee()));
        txtMonthlyFee.setText(String.format("%.2f", (float)bond.getMonthlyFee()));
        double rate = bond.getFixedRate();
        if (rate > 0.0) {
            txtRateSetting.setText(String.format("%4.2f", (float)rate));
            rateOptions[1].setSelected(true);
            lblRateSetting.setText("    %");
        } else {
            rate = bond.getPrimeDeltaRate();
            txtRateSetting.setText(String.format("%+.2f", (float)rate));
            rateOptions[0].setSelected(true);
            lblRateSetting.setText("  +/-");
        }
    }
    
    /** Creates a new instance of BondCalculatorGui */
    public BondCalculatorGui(Bond bond) {
        this.bond = bond;
        initGui();
        reset();
    }
    
    public void show() {
        frame.setVisible( true );
    }
    
}
