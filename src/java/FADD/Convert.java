/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package FADD;

import javax.swing.JLabel;
import javax.swing.JTextField;

/**
 *
 * @author RACG
 */


public interface Convert{
	public String toXML(JLabel[] jl, JTextField[] jtf);
	public String toXML(String x, String sp1, String sp2);
	public String toText(String xml, String sp);
	public String [] getLabels_from_Text(String x, String sp);
	public String [] getFields_from_Text(String x, String sp);
	public String [] getLabels_from_XML(String x);
	public String [] getFields_from_XML(String x);
}
