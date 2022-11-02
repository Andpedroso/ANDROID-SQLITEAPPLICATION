## SQLiteApplication
#### Uma aplicação prática do uso do SQLite do Android.
#### Lista de itens adicionados pelo usuário.
#### SQLite é um banco de dados local de todo Android.
---
## Projeto
---
## Classe modelo
---
> Classe
#### Tarefa
```bash
package andpedroso.android.sqliteapplication.model;

import java.io.Serializable;

public class Tarefa implements Serializable {
    private Long id;
    private String nomeTarefa;
    public Long getId() {
        return id;
    }
    public void setId(Long id) {
        this.id = id;
    }
    public String getNomeTarefa() {
        return nomeTarefa;
    }
    public void setNomeTarefa(String nomeTarefa) {
        this.nomeTarefa = nomeTarefa;
    }
}
```
---
## Helpers
---
> Gerenciar DataBase
#### DbHelper
```bash
package andpedroso.android.sqliteapplication.helper;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class DbHelper extends SQLiteOpenHelper {
    public static int VERSION = 1;
    public static String NOME_DB = "DB_TAREFAS";
    public static String TABELA_TAREFAS = "tarefas";
    public DbHelper(Context context) {
        super(context, NOME_DB, null, VERSION);
    }
    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        String sql = "CREATE TABLE IF NOT EXISTS " +
                TABELA_TAREFAS +
                " (id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "nome TEXT NOT NULL); ";
        try {
            sqLiteDatabase.execSQL(sql);
            Log.i("INFO DB", "Sucesso ao criar tabela");
        } catch (Exception e) {
            Log.i("INFO DB", "Erro ao criar tabela " + e.getMessage());
        }
    }
    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int oldVersion, int newVersion) {
        String sql = "DROP TABLE IF EXISTS " +
                TABELA_TAREFAS +
                " ;";
        try {
            sqLiteDatabase.execSQL(sql);
            onCreate(sqLiteDatabase);
            Log.i("INFO DB", "Sucesso ao atualizar app");
        } catch (Exception e) {
            Log.i("INFO DB", "Erro ao atualizar app " + e.getMessage());
        }
    }
}
```
> Interface para métodos de requisção
#### ITarefaDAO
```bash
package andpedroso.android.sqliteapplication.helper;

import java.util.List;
import andpedroso.android.sqliteapplication.model.Tarefa;

public interface ITarefaDAO {
    public boolean salvar(Tarefa tarefa);
    public boolean atualizar(Tarefa tarefa);
    public boolean deletar(Tarefa tarefa);
    public List<Tarefa> listar();
}

```
> Métodos de requisição
#### TarefaDAO
```bash
package andpedroso.android.sqliteapplication.helper;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;
import java.util.ArrayList;
import java.util.List;
import andpedroso.android.sqliteapplication.model.Tarefa;

public class TarefaDAO implements ITarefaDAO{
    private SQLiteDatabase escreve;
    private SQLiteDatabase ler;
    public TarefaDAO(Context context) {
        DbHelper db = new DbHelper(context);
        escreve = db.getWritableDatabase();
        ler = db.getReadableDatabase();
    }
    @Override
    public boolean salvar(Tarefa tarefa) {
        ContentValues cv = new ContentValues();
        cv.put("nome", tarefa.getNomeTarefa());
        try {
            escreve.insert(DbHelper.TABELA_TAREFAS,
                    null,
                    cv);
            Log.i("INFO", "Sucesso ao salvar dados");
        } catch (Exception e) {
            Log.e("INFO", "Erro ao salvar dados" + e.getMessage());
            return false;
        }
        return true;
    }
    @Override
    public boolean atualizar(Tarefa tarefa) {
        ContentValues cv = new ContentValues();
        cv.put("nome", tarefa.getNomeTarefa());
        try {
            String[] args = {tarefa.getId().toString()};
            escreve.update(DbHelper.TABELA_TAREFAS, cv, "id=?", args);
            Log.i("INFO", "Sucesso ao atualizar dados");
        } catch (Exception e){
            Log.e("INFO", "Erro ao atualizar dados" + e.getMessage());
            return false;
        }
        return true;
    }
    @Override
    public boolean deletar(Tarefa tarefa) {
        try {
            String[] args = {tarefa.getId().toString()};
            escreve.delete(DbHelper.TABELA_TAREFAS, "id=?", args);
            Log.i("INFO", "Sucesso ao remover dados");
        } catch (Exception e){
            Log.e("INFO", "Erro ao remover dados" + e.getMessage());
            return false;
        }
        return true;
    }
    @Override
    public List<Tarefa> listar() {
        List<Tarefa> tarefas = new ArrayList<>();
        String sql = "SELECT * FROM " +
                DbHelper.TABELA_TAREFAS +
                " ;";
        Cursor c = ler.rawQuery(sql, null);
        while(c.moveToNext()){
            Tarefa tarefa = new Tarefa();
            Long id = c.getLong(c.getColumnIndex("id"));
            String nomeTarefa = c.getString(c.getColumnIndex("nome"));
            tarefa.setId(id);
            tarefa.setNomeTarefa(nomeTarefa);
            tarefas.add(tarefa);
        }
        return tarefas;
    }
}
```
---
## Lista
#### A lista realiza uma leitura de todos os itens adicionados pelo usuário.
---
> Layout da Activity
```bash
<?xml version="1.0" encoding="utf-8"?>
<androidx.constraintlayout.widget.ConstraintLayout 
    xmlns:android="http://schemas.android.com/apk/res/android"
    xmlns:app="http://schemas.android.com/apk/res-auto"
    xmlns:tools="http://schemas.android.com/tools"
    android:layout_width="match_parent"
    android:layout_height="match_parent"
    tools:context=".MainActivity">
    <androidx.recyclerview.widget.RecyclerView
        android:id="@+id/lista_recylerview"
        android:layout_width="320dp"
        android:layout_height="400dp"
        android:layout_marginStart="1dp"
        android:layout_marginTop="20dp"
        android:layout_marginEnd="1dp"
        android:foregroundGravity="center"
        app:layout_constraintEnd_toEndOf="parent"
        app:layout_constraintStart_toStartOf="parent"
        app:layout_constraintTop_toTopOf="parent" />
</androidx.constraintlayout.widget.ConstraintLayout>
```
> Layout para o RecyclerView
```bash
<?xml version="1.0" encoding="utf-8"?>
<LinearLayout xmlns:android="http://schemas.android.com/apk/res/android"
    android:layout_width="match_parent"
    android:layout_height="wrap_content"
    android:orientation="vertical">
    <TextView
        android:id="@+id/textTarefa"
        android:layout_width="match_parent"
        android:layout_height="wrap_content"
        android:text="Estudar Android" />
</LinearLayout>
```
>RecyclerView
#### TarefaAdapter
```bash
package andpedroso.android.sqliteapplication.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;
import andpedroso.android.sqliteapplication.R;
import andpedroso.android.sqliteapplication.model.Tarefa;

public class TarefaAdapter extends RecyclerView.Adapter<TarefaAdapter.MyViewHolder> {
    private List<Tarefa> listaTarefas;
    public TarefaAdapter(List<Tarefa> lista) {
        this.listaTarefas = lista;
    }
    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemLista = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.lista_tarefa_adapter, parent, false);
        return new MyViewHolder(itemLista);
    }
    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        Tarefa tarefa = listaTarefas.get(position);
        holder.tarefa.setText(tarefa.getNomeTarefa());
    }
    @Override
    public int getItemCount() {
        return this.listaTarefas.size();
    }
    public class MyViewHolder extends RecyclerView.ViewHolder{
        TextView tarefa;
        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            tarefa = itemView.findViewById(R.id.textTarefa);
        }
    }
}
```
> Configurando o RecylcerView
#### MainActivity
```bash
public void carregarListaTarefas(){
        TarefaDAO tarefaDAO = new TarefaDAO(getApplicationContext());
        listaTarefas = tarefaDAO.listar();
        tarefaAdapter = new TarefaAdapter(listaTarefas);
        RecyclerView.LayoutManager layoutManager =
                new LinearLayoutManager(getApplicationContext());
        lista_recyclerview.setLayoutManager(layoutManager);
        lista_recyclerview.setHasFixedSize(true);
        lista_recyclerview.addItemDecoration(
                new DividerItemDecoration(getApplicationContext(),
                        LinearLayout.VERTICAL));
        lista_recyclerview.setAdapter(tarefaAdapter);
    }
```
> Utilizando o método de listar
#### MainActivity
```bash
@Override
    protected void onStart() {
        super.onStart();
        carregarListaTarefas();
    }
```
---
## Evento de click no RecylerView
#### RecyclerItemClicListener
---
> Classe necessária
```bash
package andpedroso.android.sqliteapplication.helper;

import android.content.Context;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;
import androidx.recyclerview.widget.RecyclerView;

public class RecyclerItemClickListener implements RecyclerView.OnItemTouchListener {
    private OnItemClickListener mListener;
    GestureDetector mGestureDetector;
    @Override
    public boolean onInterceptTouchEvent(RecyclerView rv, MotionEvent e) {
        View childView = rv.findChildViewUnder(e.getX(), e.getY());
        if (childView != null && mListener != null && mGestureDetector.onTouchEvent(e)) {
            mListener.onItemClick(childView, rv.getChildAdapterPosition(childView));
            return true;
        }
        return false;
    }
    @Override
    public void onTouchEvent(RecyclerView rv, MotionEvent e) {
    }
    @Override
    public void onRequestDisallowInterceptTouchEvent(boolean disallowIntercept) {
    }
    public interface OnItemClickListener extends AdapterView.OnItemClickListener {
        public void onItemClick(View view, int position);
        public void onLongItemClick(View view, int position);
    }
    public RecyclerItemClickListener(Context context, final RecyclerView recyclerView, OnItemClickListener listener) {
        mListener = listener;
        mGestureDetector = new GestureDetector(context, new GestureDetector.SimpleOnGestureListener() {
            @Override
            public boolean onSingleTapUp(MotionEvent e) {
                return true;
            }
            @Override
            public void onLongPress(MotionEvent e) {
                View child = recyclerView.findChildViewUnder(e.getX(), e.getY());
                if (child != null && mListener != null) {
                    mListener.onLongItemClick(child, recyclerView.getChildAdapterPosition(child));
                }
            }
        });
    }
}
```
> Configurando os eventos de click
#### MainActivity
```bash
protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        lista_recyclerview = findViewById(R.id.lista_recylerview);
        lista_recyclerview.addOnItemTouchListener(
                new RecyclerItemClickListener(
                        getApplicationContext(),
                        lista_recyclerview,
                        new RecyclerItemClickListener.OnItemClickListener() {
                            @Override
                            public void onItemClick(View view, int position) {
                                Tarefa tarefaSelecionada = listaTarefas.get(position);
                                Intent intent = new Intent(MainActivity.this, AdicionarTarefaActivity.class);
                                intent.putExtra("tarefaSelecionada", tarefaSelecionada);
                                startActivity(intent);
                            }
                            @Override
                            public void onLongItemClick(View view, int position) {
                                tarefaSelecionada = listaTarefas.get(position);
                                AlertDialog.Builder dialog = new AlertDialog.Builder(MainActivity.this);
                                dialog.setTitle("Confirmar deleção");
                                dialog.setMessage("Deseja excluir o dado: " +
                                        tarefaSelecionada.getNomeTarefa() +
                                        " ?");
                                dialog.setPositiveButton("Sim", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i) {
                                        TarefaDAO tarefaDAO = new TarefaDAO(getApplicationContext());
                                        if (tarefaDAO.deletar(tarefaSelecionada)) {
                                            carregarListaTarefas();
                                            Toast.makeText(getApplicationContext(),
                                                    "Deleted",
                                                    Toast.LENGTH_SHORT).show();
                                        } else {
                                            Toast.makeText(getApplicationContext(),
                                                    "Not Deleted",
                                                    Toast.LENGTH_SHORT).show();
                                        }
                                    }
                                });
                                dialog.setNegativeButton("Não", null);
                                dialog.create();
                                dialog.show();
                            }
                            @Override
                            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                            }
                        }
                )
        );
    }
```
---
## Menu
#### Para criar um menu é necessário criar um diretório de valor menu e adicinar um MenuResourceFile com os itens escolhidos.
---
> Menu
```bash
<?xml version="1.0" encoding="utf-8"?>
<menu xmlns:android="http://schemas.android.com/apk/res/android"
    xmlns:app="http://schemas.android.com/apk/res-auto">
    <item
        android:id="@+id/add_item"
        android:title="adicionar"
        android:icon="@drawable/add"
        app:showAsAction="always"
        />
</menu>
```
> Tratando o menu
#### MainActivity
```bash
@Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main_activity, menu);
        return super.onCreateOptionsMenu(menu);
    }
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()){
            case R.id.add_item:
                Intent intent = new Intent(getApplicationContext(), AdicionarTarefaActivity.class);
                startActivity(intent);
                break;
        }
        return super.onOptionsItemSelected(item);
    }
```
---
## Post
#### Foi criada uma outra Activity para que a activity da lista fosse atualizada sempre que um novo item fosse adicionado.
---
> Layout
```bash
<?xml version="1.0" encoding="utf-8"?>
<androidx.constraintlayout.widget.ConstraintLayout
    xmlns:android="http://schemas.android.com/apk/res/android"
    xmlns:app="http://schemas.android.com/apk/res-auto"
    xmlns:tools="http://schemas.android.com/tools"
    android:layout_width="match_parent"
    android:layout_height="match_parent"
    tools:context=".AdicionarTarefaActivity">
    <com.google.android.material.textfield.TextInputLayout
        android:layout_width="409dp"
        android:layout_height="wrap_content"
        app:layout_constraintEnd_toEndOf="parent"
        app:layout_constraintStart_toStartOf="parent"
        app:layout_constraintTop_toTopOf="parent">
        <com.google.android.material.textfield.TextInputEditText
            android:id="@+id/text_input"
            android:layout_width="match_parent"
            android:layout_height="wrap_content"
            android:gravity="center"
            android:hint="Digite uma tarefa" />
    </com.google.android.material.textfield.TextInputLayout>
</androidx.constraintlayout.widget.ConstraintLayout>
```
> Activity
#### AdicionarTarefaActivity
```bash
package andpedroso.android.sqliteapplication;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;
import com.google.android.material.textfield.TextInputEditText;
import andpedroso.android.sqliteapplication.helper.TarefaDAO;
import andpedroso.android.sqliteapplication.model.Tarefa;

public class AdicionarTarefaActivity extends AppCompatActivity {
    private TextInputEditText editTarefa;
    private Tarefa tarefaAtual;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_adicionar_tarefa);
        editTarefa = findViewById(R.id.text_input);
        tarefaAtual = (Tarefa) getIntent().getSerializableExtra("tarefaSelecionada");
        if (tarefaAtual != null) {
            editTarefa.setText(tarefaAtual.getNomeTarefa());
        }
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_adicionar_tarefa, menu);
        return super.onCreateOptionsMenu(menu);
    }
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()){
            case R.id.save_item:
                TarefaDAO tarefaDAO = new TarefaDAO(getApplicationContext());
                if (tarefaAtual != null) {
                    String nomeTarefa = editTarefa.getText().toString();
                    if (!nomeTarefa.isEmpty()) {
                        Tarefa tarefa = new Tarefa();
                        tarefa.setNomeTarefa(nomeTarefa);
                        tarefa.setId(tarefaAtual.getId());
                        if (tarefaDAO.atualizar(tarefa)){
                            Toast.makeText(getApplicationContext(),
                                    "Updated",
                                    Toast.LENGTH_SHORT).show();
                            finish();
                        } else {
                            Toast.makeText(getApplicationContext(),
                                    "Not Updated",
                                    Toast.LENGTH_SHORT).show();
                        }

                    }
                } else {
                    String nomeTarefa = editTarefa.getText().toString();
                    if (!nomeTarefa.isEmpty()) {
                        Tarefa tarefa = new Tarefa();
                        tarefa.setNomeTarefa(nomeTarefa);
                        if (tarefaDAO.salvar(tarefa)) {
                            Toast.makeText(getApplicationContext(),
                                    "Saved",
                                    Toast.LENGTH_SHORT).show();
                            finish();
                        } else {
                            Toast.makeText(getApplicationContext(),
                                    "Not saved",
                                    Toast.LENGTH_SHORT).show();
                        }

                    }
                }
                break;
            case R.id.home_item:
                Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                startActivity(intent);
                break;
        }
        return super.onOptionsItemSelected(item);
    }
}
```
---
## Problemas
#### A lista comporta String não havendo nenhuma restição de caracteres.
---
## Créditos
---
## Desenvolvedor
#### André Moura Pedroso
#### Full-Stack, Mobile e Games
#### Formação: SENAI e Faculdade Descomplica
## Tutor
#### Jamilton Damasceno
#### Desenvolvimento Android 2018 - Aprenda a criar 15 apps