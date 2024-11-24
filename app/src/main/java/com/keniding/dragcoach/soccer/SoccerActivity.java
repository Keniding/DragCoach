package com.keniding.dragcoach.soccer;

import android.app.Dialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton;
import com.google.android.material.textfield.TextInputEditText;
import com.keniding.dragcoach.R;
import com.keniding.dragcoach.soccer.model.Formation;
import com.keniding.dragcoach.soccer.model.PlayerPosition;
import com.keniding.dragcoach.soccer.utils.FormationManager;
import com.keniding.dragcoach.soccer.view.FieldView;
import com.keniding.dragcoach.soccer.view.PlayerView;

import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;

public class SoccerActivity extends AppCompatActivity {
    private FieldView fieldView;
    private ExtendedFloatingActionButton fabAddHomePlayer;
    private ExtendedFloatingActionButton fabAddAwayPlayer;
    private int homePlayerCounter = 1;
    private int awayPlayerCounter = 1;
    private FormationManager formationManager;
    private final Set<Integer> homeUsedNumbers = new HashSet<>();
    private final Set<Integer> awayUsedNumbers = new HashSet<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_soccer);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        initializeViews();
        setupListeners();
        formationManager = new FormationManager(this);
    }

    private void initializeViews() {
        fieldView = findViewById(R.id.fieldView);
        fabAddHomePlayer = findViewById(R.id.fabAddHomePlayer);
        fabAddAwayPlayer = findViewById(R.id.fabAddAwayPlayer);
        ImageButton btnSave = findViewById(R.id.btnSave);
        ImageButton btnLoad = findViewById(R.id.btnLoad);

        btnSave.setOnClickListener(v -> showSaveFormationDialog());
        btnLoad.setOnClickListener(v -> showLoadFormationDialog());

        // Configurar el listener para eliminar jugadores fieldView.setOnPlayerLongClickListener(this::showDeletePlayerDialog);
    }

    private void setupListeners() {
        fabAddHomePlayer.setOnClickListener(v -> showAddPlayerDialog(true));
        fabAddAwayPlayer.setOnClickListener(v -> showAddPlayerDialog(false));
    }

    private void showDeletePlayerDialog(PlayerView player) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Eliminar Jugador")
                .setMessage("¿Desea eliminar al jugador #" + player.getNumber() + "?")
                .setPositiveButton("Eliminar", (dialog, which) -> {
                    // Eliminar el jugador del conjunto de números usados
                    if (player.isHomeTeam()) {
                        homeUsedNumbers.remove(player.getNumber());
                    } else {
                        awayUsedNumbers.remove(player.getNumber());
                    }
                    // Eliminar el jugador del campo
                    fieldView.removePlayer(player);
                    Toast.makeText(this, "Jugador eliminado", Toast.LENGTH_SHORT).show();
                })
                .setNegativeButton("Cancelar", null)
                .show();
    }

    private void showAddPlayerDialog(boolean isHome) {
        Dialog dialog = new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.dialog_add_player);

        Window window = dialog.getWindow();
        if (window != null) {
            window.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            window.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        }

        TextView tvTitulo = dialog.findViewById(R.id.tvTitulo);
        TextInputEditText etNombreJugador = dialog.findViewById(R.id.etNombreJugador);
        TextInputEditText etNumeroJugador = dialog.findViewById(R.id.etNumeroJugador);
        MaterialButton btnAgregar = dialog.findViewById(R.id.btnAgregar);
        ImageButton btnCerrar = dialog.findViewById(R.id.btnCerrar);

        tvTitulo.setText(isHome ? "Agregar Jugador Local" : "Agregar Jugador Visitante");

        int nextNumber = findNextAvailableNumber(isHome);
        etNumeroJugador.setText(String.valueOf(nextNumber));

        btnAgregar.setOnClickListener(v -> {
            String nombre = Objects.requireNonNull(etNombreJugador.getText()).toString().trim();
            String numeroStr = Objects.requireNonNull(etNumeroJugador.getText()).toString().trim();

            if (nombre.isEmpty()) {
                etNombreJugador.setError("Ingrese el nombre del jugador");
                return;
            }

            if (numeroStr.isEmpty()) {
                etNumeroJugador.setError("Ingrese el número del jugador");
                return;
            }

            int numero;
            try {
                numero = Integer.parseInt(numeroStr);
                if ((isHome && homeUsedNumbers.contains(numero)) ||
                        (!isHome && awayUsedNumbers.contains(numero))) {
                    etNumeroJugador.setError("Este número ya está en uso");
                    return;
                }
            } catch (NumberFormatException e) {
                etNumeroJugador.setError("Número inválido");
                return;
            }

            addNewPlayer(isHome, nombre, numero);
            dialog.dismiss();
        });

        btnCerrar.setOnClickListener(v -> dialog.dismiss());
        dialog.show();
    }

    private int findNextAvailableNumber(boolean isHome) {
        Set<Integer> usedNumbers = isHome ? homeUsedNumbers : awayUsedNumbers;
        int counter = 1;
        while (usedNumbers.contains(counter)) {
            counter++;
        }
        return counter;
    }

    private void addNewPlayer(boolean isHome, String nombre, int numero) {
        PlayerView player = new PlayerView(this);
        player.setTeamColor(isHome);
        player.setNumber(numero);
        player.setPosition(nombre);

        if (isHome) {
            homeUsedNumbers.add(numero);
            homePlayerCounter = Math.max(homePlayerCounter, numero + 1);
        } else {
            awayUsedNumbers.add(numero);
            awayPlayerCounter = Math.max(awayPlayerCounter, numero + 1);
        }

        float centerX = fieldView.getWidth() / 2f;
        float centerY = fieldView.getHeight() / 2f;
        if (isHome) {
            centerY = fieldView.getHeight() * 0.75f;
        } else {
            centerY = fieldView.getHeight() * 0.25f;
        }

        player.setPosition(centerX, centerY);
        fieldView.addPlayer(player);
    }

    private void addNewPlayer(boolean isHome, String nombre, int numero, float x, float y) {
        PlayerView player = new PlayerView(this);
        player.setTeamColor(isHome);
        player.setNumber(numero);
        player.setPosition(nombre);
        player.setPosition(x, y);

        if (isHome) {
            homeUsedNumbers.add(numero);
        } else {
            awayUsedNumbers.add(numero);
        }

        fieldView.addPlayer(player);
    }

    private void showSaveFormationDialog() {
        Dialog dialog = new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.dialog_save_formation);

        Window window = dialog.getWindow();
        if (window != null) {
            window.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            window.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        }

        TextInputEditText etFormationName = dialog.findViewById(R.id.etFormationName);
        MaterialButton btnSave = dialog.findViewById(R.id.btnSave);
        ImageButton btnCerrar = dialog.findViewById(R.id.btnCerrar);

        btnSave.setOnClickListener(v -> {
            String name = etFormationName.getText().toString().trim();
            if (!name.isEmpty()) {
                saveCurrentFormation(name);
                dialog.dismiss();
            } else {
                etFormationName.setError("Ingrese un nombre para la formación");
            }
        });

        btnCerrar.setOnClickListener(v -> dialog.dismiss());

        dialog.show();
    }

    private void saveCurrentFormation(String name) {
        Formation formation = new Formation(name);
        List<PlayerPosition> homePositions = fieldView.saveFormation();
        formation.getHomePlayers().addAll(homePositions);
        formationManager.saveFormation(formation);
        Toast.makeText(this, "Formación guardada", Toast.LENGTH_SHORT).show();
    }

    private void showLoadFormationDialog() {
        List<String> formationNames = formationManager.getAllFormationNames();
        if (formationNames.isEmpty()) {
            Toast.makeText(this, "No hay formaciones guardadas", Toast.LENGTH_SHORT).show();
            return;
        }

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Cargar formación")
                .setItems(formationNames.toArray(new String[0]), (dialog, which) -> {
                    String selectedName = formationNames.get(which);
                    loadFormation(selectedName);
                })
                .show();
    }

    private void loadFormation(String name) {
        Formation formation = formationManager.loadFormation(name);
        if (formation != null) {
            fieldView.clearPlayers();
            homeUsedNumbers.clear();
            awayUsedNumbers.clear();

            for (PlayerPosition pos : formation.getHomePlayers()) {
                addNewPlayer(true, pos.getPosition(), pos.getNumber(), pos.getX(), pos.getY());
            }
            Toast.makeText(this, "Formación cargada", Toast.LENGTH_SHORT).show();
        }
    }
}
