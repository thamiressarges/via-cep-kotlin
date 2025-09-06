package com.example.viacep

import android.graphics.Color
import android.os.Bundle
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.viacep.api.Api
import com.example.viacep.databinding.ActivityMainBinding
import com.example.viacep.model.Endereco
import okhttp3.Callback
import retrofit2.Call
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.create


class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        enableEdgeToEdge()
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        //Configuração do retrofit
        val retrofit = Retrofit.Builder()
            .addConverterFactory(GsonConverterFactory.create())
            .baseUrl("https://viacep.com.br/ws/")
            .build()
            .create(Api::class.java)

        binding.btnBuscarCep.setOnClickListener {
            val cep = binding.edtCep.text.toString()

            if (cep.isEmpty()){
                Toast.makeText(this, "Preencha a cep!", Toast.LENGTH_SHORT).show()
            }else {
                retrofit.setEndereco(cep).enqueue(object : retrofit2.Callback<Endereco>{
                    override fun onResponse(
                        call: Call<Endereco?>,
                        response: Response<Endereco?>
                    ) {
                        if(response.code() == 200){
                            val logradouro = response.body()?.logradouro.toString()
                            val bairro = response.body()?.bairro.toString()
                            val localidade = response.body()?.localidade.toString()
                            val uf = response.body()?.uf.toString()

                            setFormularios(logradouro, bairro, localidade, uf)
                        }else{
                            Toast.makeText(applicationContext, "Cep inválido", Toast.LENGTH_SHORT).show()
                        }
                    }

                    override fun onFailure(
                        call: Call<Endereco?>,
                        t: Throwable
                    ) {
                        Toast.makeText(applicationContext, "Erro inesperado", Toast.LENGTH_SHORT).show()
                    }

                })
            }
        }
    }

    private fun setFormularios(logradouro: String, bairro: String, localidade: String, uf: String){
        binding.edtLogradouro.setText(logradouro)
        binding.edtBairro.setText(bairro)
        binding.edtCidade.setText(localidade)
        binding.edtEstado.setText(uf)
    }
}