package com.example.share2care

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.firestore.FirebaseFirestore

class FirestoreViewModel : ViewModel() {

    private val firestore: FirebaseFirestore = FirebaseFirestore.getInstance()

    data class LojaSocialData(val nome: String, val descricao: String)

    private val _lojaSocialData = MutableLiveData<LojaSocialData>()
    val lojaSocialData: LiveData<LojaSocialData> = _lojaSocialData

    private val _firestoreState = MutableLiveData<FirestoreState>()
    val firestoreState: LiveData<FirestoreState> = _firestoreState

    // Função para quando registar-mos um user criar os seguintes dados :
    //   - Nome -> Nome da Loja Social
    //   - Descrição -> Descrição da Loja Social
    //   - Status -> Status da aprovação da conta
    fun saveLojaSocialToFirestore(uid: String) {
        val lojaSocialMap = mapOf(
            "nome" to "",
            "descricao" to "",
            "status" to "Pendente"
        )

        firestore.collection("lojaSocial").document(uid).set(lojaSocialMap)
            .addOnSuccessListener {
                _firestoreState.value = FirestoreState.Success
            }
            .addOnFailureListener { e ->
                _firestoreState.value = FirestoreState.Error("Erro ao salvar dados da Loja Social: ${e.message}")
            }
    }

    // Função para atualizar dados da Loja Social
    fun updateLojaSocialDetails(uid: String, nome: String, descricao: String) {
        val updatedData = mapOf(
            "nome" to nome,
            "descricao" to descricao
        )

        firestore.collection("lojaSocial").document(uid).update(updatedData)
            .addOnSuccessListener {
                _firestoreState.value = FirestoreState.Success
            }
            .addOnFailureListener { e ->
                _firestoreState.value = FirestoreState.Error("Erro ao atualizar dados da Loja Social: ${e.message}")
            }
    }

    //Recolher dados da Loja Social
    fun getLojaSocialDetails(uid: String) {
        firestore.collection("lojaSocial").document(uid).get()
            .addOnSuccessListener { document ->
                if (document.exists()) {
                    val nome = document.getString("nome") ?: ""
                    val descricao = document.getString("descricao") ?: ""
                    _lojaSocialData.value = LojaSocialData(nome, descricao)
                } else {
                    _firestoreState.value = FirestoreState.Error("Loja Social não encontrada")
                }
            }
            .addOnFailureListener { e ->
                _firestoreState.value = FirestoreState.Error("Erro ao carregar dados da Loja Social: ${e.message}")
            }
    }
}

sealed class FirestoreState {
    object Success : FirestoreState()
    data class Error(val message: String) : FirestoreState()
}
