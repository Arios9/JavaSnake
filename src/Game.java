
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.WindowConstants;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.LinkedList;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;

public class Game extends JFrame {

    private final int panel_height=20, panel_width=30, snake_starting_length=20, period=50,
    right =KeyEvent.VK_RIGHT, left =KeyEvent.VK_LEFT, up =KeyEvent.VK_UP, down =KeyEvent.VK_DOWN;
    private int last_key_pressed = right,last_key_performed = right;

    private LinkedList<Square>snake=new LinkedList<>();
    private Square[][] pin=new Square[panel_height][panel_width];
    private Random random_food=new Random();
    private Timer timer=new Timer();
    private JPanel main_panel;

    private Game() {
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        setSize(new Dimension(panel_width*50, panel_height*50));
        addKeyListener(new MyKeyAdapter());
        set_panels();
        create_snake();
        cook_food();
        timer.schedule(new TimerTask(){
            public void run(){
                snake_action();
            }
        },0,period);
    }

    public static void main(String[] args) {
        new Game().setVisible(true);
    }

    private void set_panels() {
        add(main_panel=new JPanel(new GridLayout(panel_height, panel_width)));
        for(int i=0; i<panel_height; i++)
            for(int j=0; j<panel_width; j++)
                main_panel.add(pin[i][j]=new Square(i,j));
    }


    private void create_snake() {
        for(int j=0; j<snake_starting_length; j++){
            pin[9][j].setBackground(Color.green);
            pin[9][j].has_snake=true;
            snake.addFirst(pin[9][j]);
        }
    }

    private void cook_food(){
        int fi,fj;
        do{
            fi=random_food.nextInt(panel_height);
            fj=random_food.nextInt(panel_width);
        }while(pin[fi][fj].has_snake);
        pin[fi][fj].has_food=true;
        pin[fi][fj].setBackground(Color.red);
    }


    private void snake_action() {
        int i=snake.getFirst().i;
        int j=snake.getFirst().j;

        switch (last_key_pressed) {
            case right -> j++;
            case left -> j--;
            case up -> i--;
            case down -> i++;
        }

        i=(i+panel_height)%panel_height;
        j=(j+panel_width)%panel_width;

        last_key_performed = last_key_pressed;

        if (!pin[i][j].has_food) snake_moves(pin[i][j]);
        else snake_eats(pin[i][j]);
    }


    private void snake_moves(Square moves_at){
        Square temp=snake.removeLast();
        temp.has_snake=false;
        if(moves_at.has_snake){timer.cancel();return;}//gameover
        temp.setBackground(null);
        snake.addFirst(moves_at);
        moves_at.setBackground(Color.green);
        moves_at.has_snake=true;
    }

    private void snake_eats(Square moves_at){
        snake.addFirst(moves_at);
        moves_at.setBackground(Color.green);
        moves_at.has_snake=true;
        moves_at.has_food=false;
        cook_food();
    }


    private class Square extends JPanel{
        private final int i,j;
        private boolean has_snake=false, has_food=false;

        private Square(int i, int j) {
            this.i = i;
            this.j = j;
        }
    }

    private class MyKeyAdapter extends KeyAdapter{
        public void keyPressed(KeyEvent e){
            int key_code = e.getKeyCode();
            if(key_code>=37&&key_code<=40)//key code range
                if(Math.abs(key_code- last_key_performed)!=2)//not opposite directions
                    last_key_pressed=key_code;
        }
    }

}
