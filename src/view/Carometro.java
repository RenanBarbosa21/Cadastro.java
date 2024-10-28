package view;

import java.awt.EventQueue;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import javax.swing.filechooser.FileNameExtensionFilter;
import model.DAO;
import utils.validador;
import java.awt.Toolkit;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.text.DateFormat;
import java.util.Date;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.FileInputStream;
import java.awt.SystemColor;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JTextField;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import javax.swing.border.BevelBorder;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.Color;
import java.awt.Font;
import java.awt.Image;

public class Carometro extends JFrame {

    // para definir objetos do JDBC e mexer na conexao do banco de dados
    DAO dao = new DAO();
    private Connection con;
    private PreparedStatement pst;

    // para instanciar o objeto para o fluxo bytes
    private FileInputStream fis;

    // Variavel para guardar o tamanho da imagem em bytes
    private int tamanho;

    private static final long serialVersionUID = 1L;
    private JPanel contentPane;
    private JLabel lblstatus;
    private JTextField txtRA;
    private JLabel lblNewLabel_1;
    private JTextField txtNome;
    private JLabel lblfoto;
    private JLabel lbldata;
    private JButton btnNewButton;

    /**
     * Launch the application.
     */
    public static void main(String[] args) {
        EventQueue.invokeLater(new Runnable() {
            public void run() {
                try {
                    Carometro frame = new Carometro();
                    frame.setVisible(true);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    /**
     * Create the frame.
     */
    public Carometro() {
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowActivated(WindowEvent e) {
                setarData();
                status();
            }
        });
        setTitle("Carometro\\registros");
        setResizable(false);
        setIconImage(Toolkit.getDefaultToolkit().getImage(Carometro.class.getResource("/imagen/camera.png")));
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setBounds(100, 100, 716, 398);
        contentPane = new JPanel();
        contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));

        setContentPane(contentPane);
        contentPane.setLayout(null);

        JPanel btncarregar = new JPanel();
        btncarregar.setBackground(SystemColor.textHighlight);
        btncarregar.setBounds(0, 300, 700, 89);
        contentPane.add(btncarregar);
        btncarregar.setLayout(null);

        lblstatus = new JLabel("");
        lblstatus.setIcon(new ImageIcon(Carometro.class.getResource("/imagen/excluir-banco-de-dados (1).png")));
        lblstatus.setBounds(650, 7, 40, 48);
        btncarregar.add(lblstatus);

        lbldata = new JLabel("");
        lbldata.setForeground(Color.WHITE);
        lbldata.setFont(new Font("Times New Roman", Font.ITALIC, 18));
        lbldata.setBounds(30, 18, 375, 25);
        btncarregar.add(lbldata);

        JLabel lblNewLabel = new JLabel("RA");
        lblNewLabel.setBounds(40, 26, 46, 14);
        contentPane.add(lblNewLabel);

        txtRA = new JTextField();
        txtRA.setForeground(Color.BLACK);
        txtRA.addKeyListener(new KeyAdapter() {
            @Override
            public void keyTyped(KeyEvent e) {
                // codigo para colocar somente os caracteres pela orden do .contains.
                String caracteres = "0123456789";
                if (!caracteres.contains(e.getKeyChar() + "")) {
                    e.consume();
                }
            }
        });
        txtRA.setBounds(83, 23, 122, 29);
        contentPane.add(txtRA);
        txtRA.setColumns(10);
        // limitando a escrita com o plaindocument
        txtRA.setDocument(new validador(6));

        lblNewLabel_1 = new JLabel("Nome");
        lblNewLabel_1.setBounds(40, 82, 46, 14);
        contentPane.add(lblNewLabel_1);

        txtNome = new JTextField();
        txtNome.setBounds(83, 76, 327, 29);
        contentPane.add(txtNome);
        txtNome.setColumns(10);
        txtNome.setDocument(new validador(30));

        lblfoto = new JLabel("");
        lblfoto.setToolTipText("Foto aluno");
        lblfoto.setBorder(new BevelBorder(BevelBorder.LOWERED, null, null, null, null));
        lblfoto.setIcon(new ImageIcon(Carometro.class.getResource("/imagen/caera-grande.png")));
        lblfoto.setBounds(434, 23, 256, 256);
        contentPane.add(lblfoto);

        btnNewButton = new JButton("Carregar foto");
        btnNewButton.setFont(new Font("Times New Roman", Font.BOLD, 12));
        btnNewButton.setForeground(SystemColor.textHighlight);
        btnNewButton.setBounds(275, 122, 122, 29);
        contentPane.add(btnNewButton);

        JButton btnAdicionar = new JButton("");
        btnAdicionar.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                adicionar();
            }
        });
        btnAdicionar.setToolTipText("adicionar");
        btnAdicionar.setIcon(new ImageIcon(Carometro.class.getResource("/imagen/botao-adicionar.png")));
        btnAdicionar.setBounds(10, 206, 64, 64);
        contentPane.add(btnAdicionar);

        JButton bntlimpar = new JButton("");
        bntlimpar.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                reset();
            }
        });
        bntlimpar.setIcon(new ImageIcon(Carometro.class.getResource("/imagen/borracha.png")));
        bntlimpar.setToolTipText("apagar");
        bntlimpar.setBounds(303, 206, 64, 64);
        contentPane.add(bntlimpar);
        
        JButton btnBuscar = new JButton("Buscar");
        btnBuscar.setForeground(SystemColor.textHighlight);
        btnBuscar.setFont(new Font("Times New Roman", Font.BOLD, 12));
        btnBuscar.setBounds(245, 23, 103, 29);
        contentPane.add(btnBuscar);

        // para acionar o botao.
        btnNewButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                carregarfoto();
            }
        });
    }

    // teste direto do banco de dados atraves do trycath
    private void status() {
        try {
            con = dao.conectar();
            if (con == null) {
                // System.out.println("deu erro de conecxão meu patrao");
                lblstatus.setIcon(new ImageIcon(Carometro.class.getResource("/imagen/excluir-banco-de-dados (1).png")));
            } else {
                // System.out.println("tudo certo/conectado meu patrao");
                lblstatus.setIcon(new ImageIcon(
                        Carometro.class.getResource("/imagen/simbolo-de-banco-de-dados-em-branco (1).png")));
            }
            con.close();
        } catch (Exception e) {
            System.out.println(e);
        }
    }

    // programa para mostrar a data(formatador e bases[dateformat])
    private void setarData() {
        Date data = new Date();
        DateFormat formatador = DateFormat.getDateInstance(DateFormat.FULL);
        lbldata.setText(formatador.format(data));
    }

    private void carregarfoto() {
        JFileChooser jfc = new JFileChooser();
        jfc.setDialogTitle("Selecione foto");
        jfc.setFileFilter(new FileNameExtensionFilter("Arquivos de imagens(*.PNG,*.JPG,*.JPEG)", "png", "jpg", "jpeg"));
        int resultado = jfc.showOpenDialog(this);
        if (resultado == JFileChooser.APPROVE_OPTION) {
            try {
                fis = new FileInputStream(jfc.getSelectedFile());
                tamanho = (int) jfc.getSelectedFile().length();
                Image foto = ImageIO.read(jfc.getSelectedFile()).getScaledInstance(lblfoto.getWidth(), lblfoto.getHeight(),
                        Image.SCALE_SMOOTH);
                lblfoto.setIcon(new ImageIcon(foto));
                lblfoto.updateUI();
            } catch (Exception e) {
                System.out.println(e);
            }
        }
    }

    private void adicionar() {
        if (txtNome.getText().isEmpty()) {
            JOptionPane.showMessageDialog(null, "Colocar o nome meu patrao!");
            txtNome.requestFocus();
        } else {
            String insert = "insert into alunos (nome,foto) values(?,?)";
            try {
                con = dao.conectar();
                pst = con.prepareStatement(insert);
                pst.setString(1, txtNome.getText());
                pst.setBlob(2, fis, tamanho);
                int confirma = pst.executeUpdate();
                if (confirma == 1) {
                    JOptionPane.showMessageDialog(null, "cadastro do aluno realizado.");
                    reset();
                } else {
                    JOptionPane.showMessageDialog(null, "deu erro no cadastro meu patrao.");
                }
                con.close();
            } catch (Exception e) {
                System.out.println(e);
            }
        }
    }

    private void reset() {
        txtRA.setText(null);
        txtNome.setText(null);
        lblfoto.setIcon(new ImageIcon(Carometro.class.getResource("/imagen/caera-grande.png")));
        txtNome.requestFocus();
    }
}
