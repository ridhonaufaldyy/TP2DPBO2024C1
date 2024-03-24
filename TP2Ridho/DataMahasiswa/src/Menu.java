import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.sql.*;

public class Menu extends JFrame{
    public static void main(String[] args) {
        // buat object window
        Menu window = new Menu();

        // atur ukuran window
        window.setSize(400, 300);
        // letakkan window di tengah layar
        window.setLocationRelativeTo(null);
        // isi window
        window.setContentPane(window.mainPanel);
        // ubah warna background
        window.getContentPane().setBackground(Color.white);
        // tampilkan window
        window.setVisible(true);
        // agar program ikut berhenti saat window diclose
        window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    }

    // index baris yang diklik
    private int selectedIndex = -1;
    // list untuk menampung semua mahasiswa
    private ArrayList<Mahasiswa> listMahasiswa;

    private Database database;

    private String RadioButtonDipiih = ""; // untuk menentukan RadioButton

    private JPanel mainPanel;
    private JTextField nimField;
    private JTextField namaField;
    private JTable mahasiswaTable;
    private JButton addUpdateButton;
    private JButton cancelButton;
    private JComboBox jenisKelaminComboBox;
    private JButton deleteButton;
    private JLabel titleLabel;
    private JLabel nimLabel;
    private JLabel namaLabel;
    private JLabel jenisKelaminLabel;
    private JTextField angkatanField;
    private JLabel angkatanLabel;
    private JRadioButton nimRadioButton;
    private JRadioButton namaRadioButton;
    private JTextField searchField;
    private JTextField searchnamaField;
    private JButton filterButton;

    // constructor
    public Menu() {
        // inisialisasi listMahasiswa
        listMahasiswa = new ArrayList<>();

        // objek databse
        database = new Database();

        /* membuat button agar bisa hanya dipilih satu */
        // Buat objek ButtonGroup
        ButtonGroup buttonGroup = new ButtonGroup();
        // Tambahkan RadioButton ke ButtonGroup
        buttonGroup.add(nimRadioButton);
        buttonGroup.add(namaRadioButton);

        // isi listMahasiswa
        populateList();

        // isi tabel mahasiswa
        mahasiswaTable.setModel(setTable());

        // ubah styling title
        titleLabel.setFont(titleLabel.getFont().deriveFont(Font.BOLD,20f));

        // atur isi combo box
        String[] jenisKelaminData ={"Laki-Laki", "Perempuan"};
        jenisKelaminComboBox.setModel(new DefaultComboBoxModel(jenisKelaminData));

        // sembunyikan button delete
        deleteButton.setVisible(false);

        // menambah filter
        filterButton.setVisible(false);
        searchField.setVisible(false);

        // saat tombol add/update ditekan
        addUpdateButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if(selectedIndex == -1)
                {
                    insertData();
                }
                else
                {
                    updateData();
                }
            }
        });
        // saat tombol delete ditekan
        deleteButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if(selectedIndex >=0)
                {
                    deleteData();
                }
            }
        });


        filterButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String search = searchField.getText(); // input untuk mencari
                if(!search.isEmpty()) // jika memang terdapat data
                {
                    String sql = "";

                    if(RadioButtonDipiih.equals("NIM"))
                    {
                        sql = "SELECT * FROM Mahasiswa WHERE nim = '" + search + "'";
                    }
                    else if(RadioButtonDipiih.equals("Nama"))
                    {
                        sql = "SELECT * FROM Mahasiswa WHERE nama = '" + search + "'";
                    }
                    ResultSet rs = database.selectQuery(sql);
                    try
                    {
                        DefaultTableModel model = (DefaultTableModel) mahasiswaTable.getModel();
                        if(rs.next())
                        {
                            // mencari baris yang cocok dalam model
                            for (int i = model.getRowCount() - 1; i >= 0; i--) {
                                if (model.getValueAt(i, 1).equals(rs.getString("nim")) && RadioButtonDipiih.equals("NIM") || model.getValueAt(i, 2).equals(rs.getString("nama")) && RadioButtonDipiih.equals("Nama")) {
                                    // jika baris yang cocok ditemukan, simpan data baris
                                    Object[] row = new Object[]{model.getValueAt(i, 0), model.getValueAt(i, 1), model.getValueAt(i, 2), model.getValueAt(i, 3), model.getValueAt(i, 4)};
                                    // hapus baris dari model
                                    model.removeRow(i);
                                    // tambahkan baris ke paling atas
                                    model.insertRow(0, row);
                                }
                            }
                        }
                        else
                        { // jika tidak ketemu
                            // akan menampilkan kalau data tersebut tidak ditemukan
                            JOptionPane.showMessageDialog(null, "Data tersebut tidak ditemukan!");
                        }
                    } catch (SQLException ex) {
                        throw new RuntimeException(ex);
                    }

                    buttonGroup.clearSelection(); // atur ulang RadioButton
                    searchField.setText(""); // kosongkan field pencarian
                    searchField.setVisible(false); // sembunyikan field pencarian
                    filterButton.setVisible(false); // sembunyikan tombol filter
                    RadioButtonDipiih = ""; // kosongkan button yang dipilih
                }
                else // jika data list kosong
                {
                    JOptionPane.showMessageDialog(null, "Data Kosong!");
                }
            }
        });

        // saat tombol cancel ditekan
        cancelButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                clearForm();
            }
        });

        // saat salah satu baris tabel ditekan
        mahasiswaTable.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                // ubah selectedIndex menjadi baris tabel yang diklik
                selectedIndex = mahasiswaTable.getSelectedRow();

                // simpan value textfield dan combo box
                String selectedNim = mahasiswaTable.getModel().getValueAt(selectedIndex,1).toString();
                String selectedNama = mahasiswaTable.getModel().getValueAt(selectedIndex,2).toString();
                String selectedJenisKelamin = mahasiswaTable.getModel().getValueAt(selectedIndex,3).toString();
                String selectedAngkatan  = mahasiswaTable.getModel().getValueAt(selectedIndex,4).toString();

                // ubah isi textfield dan combo box
                nimField.setText(selectedNim);
                namaField.setText(selectedNama);
                jenisKelaminComboBox.setSelectedItem(selectedJenisKelamin);
                angkatanField.setText(selectedAngkatan);

                // ubah button "Add" menjadi "Update"
                addUpdateButton.setText("Update");
                // tampilkan button delete
                deleteButton.setVisible(true);
            }
        });
    }

    public final DefaultTableModel setTable() {
        // tentukan kolom tabel
        Object[] column = {"No", "Nim", "Nama", "Jenis Kelamin", "Angkatan"};

        // buat objek tabel dengan kolom yang sudah dibuat
        DefaultTableModel temp = new DefaultTableModel(null, column);
        try {
            ResultSet resultSet = database.selectQuery("SELECT * FROM mahasiswa");
            int i = 0;
            while (resultSet.next()) {
                Object[] row = new Object[5];
                row[0] = i + 1;
                row[1] = resultSet.getString("nim");
                row[2] = resultSet.getString("nama");
                row[3] = resultSet.getString("jenis_Kelamin");
                row[4] = resultSet.getString("angkatan");

                temp.addRow(row);
                i++;
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        return temp;
    }


    public void insertData() {
        String nim = nimField.getText();
        String nama = namaField.getText();
        String jenisKelamin = jenisKelaminComboBox.getSelectedItem().toString();
        String angkatan = angkatanField.getText();
        boolean nimExists = false;

        // Periksa apakah NIM sudah ada dalam listMahasiswa
        for (Mahasiswa m : listMahasiswa) {
            if (m.getNim().equals(nim)) {
                nimExists = true;
                break;
            }
        }

        if (!nim.isEmpty() && !nama.isEmpty()) {
            if (nimExists) {
                JOptionPane.showMessageDialog(null, "NIM sudah ada!");
            } else {
                // Tambahkan data baru jika NIM belum ada
                listMahasiswa.add(new Mahasiswa(nim, nama, jenisKelamin, angkatan));
                String sql = "INSERT INTO mahasiswa VALUE (null, '" + nim + "', '" + nama + "', '" + jenisKelamin + "', '" + angkatan + "');";
                database.upToNow(sql);
                mahasiswaTable.setModel(setTable());
                clearForm();
                // Geser baris ke paling bawah
                mahasiswaTable.scrollRectToVisible(mahasiswaTable.getCellRect(mahasiswaTable.getRowCount() - 1, 0, true));
                JOptionPane.showMessageDialog(null, "Data berhasil ditambahkan");
            }
        } else {
            JOptionPane.showMessageDialog(null, "Ada input yang kosong!");
        }
    }

    public void updateData() {
        String nim = nimField.getText();
        String nama = namaField.getText();
        String jenisKelamin = jenisKelaminComboBox.getSelectedItem().toString();
        String angkatan = angkatanField.getText(); // Update variabel nilai menjadi angkatan
        if (!nim.isEmpty() && !nama.isEmpty()) {
            // Periksa apakah NIM sudah ada dalam listMahasiswa
            boolean nimExists = false;
            for (Mahasiswa m : listMahasiswa) {
                if (m.getNim().equals(nim)) {
                    nimExists = true;
                    break;
                }
            }

            if (nimExists) {
                JOptionPane.showMessageDialog(null, "NIM sudah ada!");
            } else {
                int confirm = JOptionPane.showConfirmDialog(null, "Apakah Anda yakin ingin mengupdate data ini?", "Konfirmasi", JOptionPane.YES_NO_OPTION);
                if (confirm == JOptionPane.YES_OPTION) {
                    String nimSelected = listMahasiswa.get(selectedIndex).getNim();
                    listMahasiswa.get(selectedIndex).setNim(nim);
                    listMahasiswa.get(selectedIndex).setNama(nama);
                    listMahasiswa.get(selectedIndex).setJenisKelamin(jenisKelamin);
                    listMahasiswa.get(selectedIndex).setAngkatan(angkatan); // Mengatur angkatan
                    // Ubah juga query SQL untuk kolom angkatan
                    String sql = "UPDATE mahasiswa SET nim = '" + nim + "', nama = '" + nama + "', jenis_kelamin = '" + jenisKelamin + "', angkatan = '" + angkatan + "' WHERE nim = '" + nimSelected + "';";
                    database.upToNow(sql);
                    mahasiswaTable.setModel(setTable());
                    clearForm();
                    System.out.println("Update Berhasil");
                    JOptionPane.showMessageDialog(null, "Data berhasil diubah!");
                }
            }
        } else {
            JOptionPane.showMessageDialog(null, "Ada input yang kosong!");
        }
    }


    public void deleteData() {
        if (selectedIndex != -1) {
            int confirm = JOptionPane.showConfirmDialog(null, "Apakah Anda yakin ingin menghapus data ini?", "Konfirmasi", JOptionPane.YES_NO_OPTION);
            if (confirm == JOptionPane.YES_OPTION) {
                String nim = listMahasiswa.get(selectedIndex).getNim();
                listMahasiswa.remove(selectedIndex);
                String sql = "DELETE FROM mahasiswa WHERE nim = '" + nim + "';";
                database.upToNow(sql);
                mahasiswaTable.setModel(setTable());
                clearForm();
                JOptionPane.showMessageDialog(null, "Data berhasil dihapus");
            }
        } else {
            JOptionPane.showMessageDialog(null, "Pilih baris yang akan dihapus terlebih dahulu");
        }
    }




    public void clearForm() {
        // kosongkan semua texfield dan combo box
        nimField.setText("");
        namaField.setText("");
        jenisKelaminComboBox.setSelectedItem("");
        angkatanField.setText("");

        // ubah button "Update" menjadi "Add"
        addUpdateButton.setText("Add");
        // sembunyikan button delete
        deleteButton.setVisible(false);
        // ubah selectedIndex menjadi -1 (tidak ada baris yang dipilih)
        selectedIndex = -1;

        searchField.setText(""); // kosongkan field pencarian
        searchField.setVisible(false); // sembunyikan field pencarian
        filterButton.setVisible(false); // sembunyikan tombol filter
        RadioButtonDipiih = "";

    }

    private void populateList() {
        listMahasiswa.add(new Mahasiswa("2203999", "Amelia Zalfa Julianti", "Perempuan", "22"));
        listMahasiswa.add(new Mahasiswa("2202292", "Muhammad Iqbal Fadhilah", "Laki-laki", "22"));
        listMahasiswa.add(new Mahasiswa("2202346", "Muhammad Rifky Afandi", "Laki-laki", "22"));
        listMahasiswa.add(new Mahasiswa("2210239", "Muhammad Hanif Abdillah", "Laki-laki", " 22"));
        listMahasiswa.add(new Mahasiswa("2202046", "Nurainun", "Perempuan", "22"));
        listMahasiswa.add(new Mahasiswa("2205101", "Kelvin Julian Putra", "Laki-laki", "22"));
        listMahasiswa.add(new Mahasiswa("2200163", "Rifanny Lysara Annastasya", "Perempuan", "22"));
        listMahasiswa.add(new Mahasiswa("2202869", "Revana Faliha Salma", "Perempuan", "22"));
        listMahasiswa.add(new Mahasiswa("2209489", "Rakha Dhifiargo Hariadi", "Laki-laki", "22"));
        listMahasiswa.add(new Mahasiswa("2203142", "Roshan Syalwan Nurilham", "Laki-laki", "22"));
        listMahasiswa.add(new Mahasiswa("2200311", "Raden Rahman Ismail", "Laki-laki", "22"));
        listMahasiswa.add(new Mahasiswa("2200978", "Ratu Syahirah Khairunnisa", "Perempuan", "22"));
        listMahasiswa.add(new Mahasiswa("2204509", "Muhammad Fahreza Fauzan", "Laki-laki", "22"));
        listMahasiswa.add(new Mahasiswa("2205027", "Muhammad Rizki Revandi", "Laki-laki", "22"));
        listMahasiswa.add(new Mahasiswa("2203484", "Arya Aydin Margono", "Laki-laki", "22"));
        listMahasiswa.add(new Mahasiswa("2200481", "Marvel Ravindra Dioputra", "Laki-laki", "22"));
        listMahasiswa.add(new Mahasiswa("2209889", "Muhammad Fadlul Hafiizh", "Laki-laki", "22"));
        listMahasiswa.add(new Mahasiswa("2206697", "Rifa Sania", "Perempuan", "22"));
        listMahasiswa.add(new Mahasiswa("2207260", "Imam Chalish Rafidhul Haque", "Laki-laki", "22"));
        listMahasiswa.add(new Mahasiswa("2204343", "Meiva Labibah Putri", "Perempuan", "22"));
    }
}
