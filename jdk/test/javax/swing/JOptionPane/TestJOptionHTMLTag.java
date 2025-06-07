/*
 * Copyright (c) 2022, Oracle and/or its affiliates. All rights reserved.
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS FILE HEADER.
 *
 * This code is free software; you can redistribute it and/or modify it
 * under the terms of the GNU General Public License version 2 only, as
 * published by the Free Software Foundation.
 *
 * This code is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or
 * FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License
 * version 2 for more details (a copy is included in the LICENSE file that
 * accompanied this code).
 *
 * You should have received a copy of the GNU General Public License version
 * 2 along with this work; if not, write to the Free Software Foundation,
 * Inc., 51 Franklin St, Fifth Floor, Boston, MA 02110-1301 USA.
 *
 * Please contact Oracle, 500 Oracle Parkway, Redwood Shores, CA 94065 USA
 * or visit www.oracle.com if you need additional information or have any
 * questions.
 */
/* @test
 * @bug 5074006
 * @key headful
 * @library /java/awt/regtesthelpers
 * @library /test/lib
 * @summary Swing JOptionPane shows <html> tag as a string after newline
 * @run main/manual TestJOptionHTMLTag
*/

import java.awt.BorderLayout;
import static java.awt.Dialog.ModalityType;

import javax.swing.JDialog;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.SwingUtilities;

import jtreg.SkippedException;

public class TestJOptionHTMLTag {
    static String instructions
            = "INSTRUCTIONS:\n" +
            "    Read the text in the above panel.\n" +
            "    If it does not contain </html> string, press Pass else press Fail.\n";

    private static boolean passed = false;
    private static boolean skipped = false;
    private static Thread mainThread = Thread.currentThread();
    public static void main(String[] args) throws Exception {

        try {
            SwingUtilities.invokeLater(() -> {
                String message = "<html>" + "This is a test\n" + "</html>\n\n\n\n";
                JOptionPane optionPane = new JOptionPane();
                optionPane.setMessage(message);
                optionPane.setMessageType(JOptionPane.INFORMATION_MESSAGE);
                optionPane.setOptions(new Object[]{"Pass", "Fail"});
                JDialog dialog = optionPane.createDialog("Test");
                dialog.setModalityType(ModalityType.TOOLKIT_MODAL);
                dialog.setContentPane(optionPane);

                JTextArea textArea = new JTextArea(instructions);
                textArea.setEditable(false);
                JPanel mainPanel = new JPanel(new BorderLayout());
                mainPanel.add(textArea, BorderLayout.CENTER);
                dialog.add(mainPanel);
                dialog.pack();
                dialog.show();
                Object value = optionPane.getValue();
                if (value == null) {
                    skipped = true;
                } else if (value.equals("Pass")) {
                    passed = true;
                }
                mainThread.interrupt();
            });
            Thread.sleep(10000);
        } catch (InterruptedException e) {
            if (passed) {
                return;
            } else if (!skipped) {
                throw new RuntimeException("Test failed.");
            }
        }
        throw new SkippedException("Test skipped.");
    }
}

