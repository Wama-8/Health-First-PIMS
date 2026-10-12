package Gui;

import java.awt.Color;


//adds theme and color to the system
public class AppColors {

    // Main brand color - used for headers, primary buttons, titles
    public static final Color PRIMARY = new Color(0, 102, 204);      
    public static final Color PRIMARY_DARK = new Color(0, 70, 150);   

    // Accent colors
    public static final Color SUCCESS = new Color(46, 139, 87);       
    public static final Color DANGER  = new Color(204, 0, 0);         
    public static final Color WARNING = new Color(230, 162, 0);       

    // Backgrounds
    public static final Color BACKGROUND       = new Color(245, 247, 250); 
    public static final Color CARD_BACKGROUND  = Color.WHITE;              
    public static final Color SIDEBAR           = new Color(25, 42, 86);   

    // Pharmacy-themed teal gradient 
    public static final Color TEAL_DARK  = new Color(0, 105, 92);   
    public static final Color TEAL_LIGHT = new Color(38, 166, 154);

    // Text
    public static final Color TEXT_DARK  = new Color(33, 33, 33);
    public static final Color TEXT_LIGHT = Color.WHITE;
    public static final Color TEXT_MUTED = new Color(120, 120, 120);

    // Table row highlighting 
    public static final Color ROW_LOW_STOCK    = new Color(255, 205, 210); // light red
    public static final Color ROW_MEDIUM_STOCK = new Color(255, 249, 196); // light yellow
    public static final Color ROW_OK_STOCK     = Color.WHITE;

        private AppColors() {
    }
}
