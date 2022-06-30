
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;

public class GUI extends JFrame{
    private String stato;
    private int quantity;
    private int test;

    final JButton refill = new JButton("Refill");
    final JButton recover = new JButton("Recover");

    private boolean GUINeedRecover = false;
    private boolean GUINeedRefill = false;

    final JLabel modality = new JLabel(this.stato);
    final JLabel itemsAvailable = new JLabel(Integer.toString(this.quantity));
    final JLabel selfTests = new JLabel(Integer.toString(this.test));
    
    public GUI(int size) {

        this.stato="idle";
        this.quantity=3;
        this.test=0;

        //rende i bottoni non cliccabili
        this.refill.setEnabled(false);
        this.recover.setEnabled(false);

        this.setDefaultCloseOperation(EXIT_ON_CLOSE);
        this.setSize(100*size, 50*size);


        final JLabel modalityLegend = new JLabel("Modality");
        final JLabel itemsAvailableLegend = new JLabel("Items available");
        final JLabel selfTestsLegend = new JLabel("Self tests");


        JPanel panel = new JPanel(null);
        this.getContentPane().add(BorderLayout.CENTER,panel);
        
        
        //position and size
        Dimension sizeB = refill.getPreferredSize();
        Dimension sizeL = selfTestsLegend.getPreferredSize();
        refill.setBounds(80,  50 * size - sizeB.height - 80, sizeB.width * 2, sizeB.height);
        recover.setBounds(280, 50 * size - sizeB.height - 80, sizeB.width * 2, sizeB.height);
        modality.setBounds(100 * size / 3 - sizeL.width * 2,  100, sizeL.width * 4, sizeL.height);
        itemsAvailable.setBounds(200 / 3 * size  - sizeL.width * 2+ 40, 100, sizeL.width * 4, sizeL.height);
        selfTests.setBounds(100 * size  - sizeL.width * 2+ 20,  100, sizeL.width * 4, sizeL.height);
        
        modalityLegend.setBounds(100 * size / 3 - sizeL.width * 2,  50, sizeL.width  * 2, sizeL.height);
        itemsAvailableLegend.setBounds(200 / 3 * size  - sizeL.width * 2, 50, sizeL.width  * 2, sizeL.height);
        selfTestsLegend.setBounds(100 * size  - sizeL.width * 2,  50, sizeL.width  * 2, sizeL.height);
        
        panel.add(refill);
        panel.add(recover);
        panel.add(modality);
        panel.add(itemsAvailable);
        panel.add(selfTests);
        panel.add(modalityLegend);
        panel.add(itemsAvailableLegend);
        panel.add(selfTestsLegend);

        ActionListener recoverListener = (e) -> {
            this.recover.setEnabled(false);
            GUINeedRecover =true;
        };

        ActionListener refillListener = (e) -> {
            this.refill.setEnabled(false);
            GUINeedRefill =true;
        };


        recover.addActionListener(recoverListener);
        refill.addActionListener(refillListener);

        this.setVisible(true);
    }


    public void update(String state, int quantity, int test){
        //metodo per aggiornare la GUI
        this.stato = state;
        this.quantity = quantity;
        this.test = test;

        this.itemsAvailable.setText(Integer.toString(this.quantity));
        this.selfTests.setText(Integer.toString(this.test));
        this.modality.setText(this.stato);
    }


    public void cloneRefill(boolean enableRefill){
        //metodo per rendere cliccabile al momento oppurtuno il bottone del refill
        if(enableRefill){
            refill.setEnabled(true);
        }
    }

    public void cloneRecover(boolean enableRecover){
        //metodo per rendere cliccabile al momento oppurtuno il bottone del recover
        if(enableRecover){
            recover.setEnabled(true);
        }
    }


    public void setRefill(boolean needs){
        //setta la varibile che abilita il refill nella GUI
        this.GUINeedRefill = needs;

    }

    public void setRecover(boolean needs){
        //setta la varibile che abilita il recover nella GUI
        this.GUINeedRecover = needs;
    }

    public boolean getRecover(){
        //ottieni variabile per il recover
        return this.GUINeedRecover;
    }

    public boolean getRefill(){
        //ottieni variabile per il refill
        return this.GUINeedRefill;
    }




}
