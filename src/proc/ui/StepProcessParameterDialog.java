package proc.ui;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Collection;
import java.util.Iterator;
import java.util.Vector;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;

import proc.param.StepProcessParameter;

public class StepProcessParameterDialog extends JDialog implements
        ActionListener {

    public boolean confirmed = false;

    public static final int STEPPROCESSPARAMETER_DONTEDIT = 2;

    public static final int STEPPROCESSPARAMETER_INVISIBLE = 1;

    private JButton btnCancel = new JButton("Anuluj");

    private JButton btnOk = new JButton("OK");

    private Vector fields = new Vector();

    private Vector params = new Vector();

    public StepProcessParameterDialog(JFrame owner, Collection pc) {
        super(owner, "Parametry");
        params.addAll(pc);
        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        getContentPane().setLayout(new BorderLayout());
        JPanel fs = new JPanel();

        int height = params.size();
        for (Iterator iter = params.iterator(); iter.hasNext();) {
            StepProcessParameter p = (StepProcessParameter) iter.next();
            if ((p.getFlags() & STEPPROCESSPARAMETER_INVISIBLE) != 0) {
                height--;
            }
        }
        fs.setLayout(new GridLayout(height, 2));

        for (Iterator iter = params.iterator(); iter.hasNext();) {
            StepProcessParameter p = (StepProcessParameter) iter.next();
            if ((p.getFlags() & STEPPROCESSPARAMETER_INVISIBLE) != 0) {
                continue;
            }
            JTextArea ta = new JTextArea(p.getName());
            ta.setLineWrap(true);
            ta.setWrapStyleWord(true);
            ta.setEditable(false);
            ta.setMaximumSize(new Dimension(400, 35));
            JScrollPane sp = new JScrollPane(ta);
            fs.add(sp);
            JTextField f = new JTextField(p.getValue().toString());
            if ((p.getFlags() & STEPPROCESSPARAMETER_DONTEDIT) != 0) {
                f.setEditable(false);
            }
            fields.add(f);
            fs.add(f);
        }
        getContentPane().add(fs, BorderLayout.CENTER);

        JPanel panel1 = new JPanel();
        panel1.setLayout(new FlowLayout());
        btnOk.addActionListener(this);
        btnCancel.addActionListener(this);
        panel1.add(btnOk);
        panel1.add(btnCancel);
        getContentPane().add(panel1, BorderLayout.SOUTH);
        setSize(500, 35 * height + 70);
        setLocationRelativeTo(owner);
    }

    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == btnOk) {
            int k = 0;
            for (int i = 0; i < params.size(); i++) {
                StepProcessParameter p = (StepProcessParameter) params.get(i);
                if ((p.getFlags() & STEPPROCESSPARAMETER_INVISIBLE) != 0)
                    continue;
                JTextField f = (JTextField) fields.get(k++);
                p.setValue(f.getText());
            }
            confirmed = true;
            dispose();
        } else if (e.getSource() == btnCancel) {
            dispose();
        }
    }

}