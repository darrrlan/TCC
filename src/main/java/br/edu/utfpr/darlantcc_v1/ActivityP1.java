/*
    Copyright (C) 2024  Lucio A. Rocha

    This program is free software: you can redistribute it and/or modify
    it under the terms of the GNU General Public License as published by
    the Free Software Foundation, either version 3 of the License, or
    (at your option) any later version.

    This program is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU General Public License for more details.

    You should have received a copy of the GNU General Public License
    along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

package br.edu.utfpr.darlantcc_v1;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import br.edu.utfpr.darlantcc_v1.model.Pessoa;
import br.edu.utfpr.darlantcc_v1.model.Resposta;
import br.edu.utfpr.darlantcc_v1.persistencia.PessoaDatabase;
import br.edu.utfpr.darlantcc_v1.persistencia.RespostaDatabase;

import android.util.Log;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

import static br.edu.utfpr.darlantcc_v1.ActivityP2.contP2;
import static br.edu.utfpr.darlantcc_v1.ActivityQuestionario.contP0;

public class ActivityP1 extends AppCompatActivity {

    private Button botaoProximo;
    private Button botaoAnterior;

    private Pessoa PESSOA;

    private String ID;
    private String ID_PESSOA;
    private String NOME;
    private String IDADE;

    private ImageView imagem1;
    private ImageView imagem2;

    private ArrayList<Resposta> listaRespostas; //local
    private RespostaDatabase respostaDatabase; //BD Room

    public static Context contP1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //Seta fullscreen
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_p1);

        /*ActionBar actionBar = getSupportActionBar();
        if (actionBar != null){
            actionBar.setDisplayHomeAsUpEnabled(true); //Cria uma nova instancia da Activity parent
        }*/

        respostaDatabase = RespostaDatabase.getDatabase(this);
        listaRespostas = (ArrayList<Resposta>) respostaDatabase.respostaDAO().queryAll();

        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();
        if (bundle != null) {
            ID = bundle.getString("CAMPO_ID");
            ID_PESSOA = bundle.getString("CAMPO_ID_PESSOA");
            NOME = bundle.getString("CAMPO_NOME");
            IDADE = bundle.getString("CAMPO_IDADE");

            Log.d("[LOG4]:","CAMPO_ID:" + ID + " CAMPO_ID_PESSOA:"+ID_PESSOA+" CAMPO_NOME:"+NOME+" CAMPO_IDADE:"+IDADE);

            setTitle(getResources().getString(R.string.questionario_titulo)+" "+
                    getResources().getString(R.string.questionario_id)+ " " +ID+", "+
                    getResources().getString(R.string.questionario_nome)+ NOME + ", " +
                    getResources().getString(R.string.questionario_idade)+IDADE);

            PESSOA = new Pessoa();
            PESSOA.setID(Integer.parseInt(ID_PESSOA));
            PESSOA.setNOME(NOME);
            PESSOA.setIDADE(IDADE);



        }

        botaoProximo = findViewById(R.id.botaoProximo);
        botaoProximo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                ActivityP2.iniciar(ActivityP1.this,PESSOA,ID);

            }
        });

        botaoAnterior = findViewById(R.id.botaoAnterior);
        botaoAnterior.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                finalizar();

            }
        });

        imagem1 = findViewById(R.id.imagem1);
        imagem1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(ActivityP1.this,
                        getResources().getString(R.string.clicou_imagem1),
                        Toast.LENGTH_LONG).show();

                atualizarResposta(1);
            }
        });

        imagem2 = findViewById(R.id.imagem2);
        imagem2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(ActivityP1.this,
                        getResources().getString(R.string.clicou_imagem2),
                        Toast.LENGTH_LONG).show();

                atualizarResposta(2);
            }
        });

        //Para finalizar no filho
        contP1=ActivityP1.this;

    }

    public void atualizarResposta(int id){
        //Adquire a tupla do banco de dados
        Resposta resposta = respostaDatabase.respostaDAO().queryForId(Integer.parseInt(ID));

        //Atualiza o campo 'desta' pergunta
        resposta.setR1(String.valueOf(id));

        //Atualiza na base de dados
        respostaDatabase.respostaDAO().update(resposta);
    }

    public static void iniciar(AppCompatActivity origem, Pessoa pessoa, String id){

        Intent intent = new Intent(origem, ActivityP1.class);

        intent.putExtra("CAMPO_ID", id);
        intent.putExtra("CAMPO_ID_PESSOA", String.valueOf(pessoa.getID()));
        intent.putExtra("CAMPO_NOME", pessoa.getNOME());
        intent.putExtra("CAMPO_IDADE",pessoa.getIDADE());

        Log.d("[LOG3]:","CAMPO_ID:" + id + " CAMPO_ID_PESSOA:"+pessoa.getID());
        origem.startActivityForResult(intent,1); //Inicia esta ActivityP1 com requestcode=1

    }

    public void finalizar() {

        //Retorno da Activity no fim da chamada
        Intent intent = new Intent();

        setResult(Activity.RESULT_CANCELED, intent);

        finish();
    }

    @Override
    public void onBackPressed() {
        finalizar();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu){
        getMenuInflater().inflate(R.menu.menu_cancelar,menu);
        return true;
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id= item.getItemId();

        if(id==R.id.menuItemCancelar) {

                finalizar();

                return true;
            } else {
                return super.onOptionsItemSelected(item);
            }

    }
}