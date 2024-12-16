package com.example.share2care

import android.net.Uri
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference

class FirestoreViewModel : ViewModel() {

    private val firestore: FirebaseFirestore = FirebaseFirestore.getInstance()
    private val storage: FirebaseStorage = FirebaseStorage.getInstance()

    data class LojaSocialData(val nome: String, val descricao: String, val imagemUrl: String? = null)

    private val _lojaSocialData = MutableLiveData<LojaSocialData>()
    val lojaSocialData: LiveData<LojaSocialData> = _lojaSocialData

    private val _firestoreState = MutableLiveData<FirestoreState>()
    val firestoreState: LiveData<FirestoreState> = _firestoreState

    fun saveLojaSocialToFirestore(uid: String) {
        val lojaSocialMap = mapOf(
            "nome" to "",
            "descricao" to "",
            "status" to "Pendente",
            "imagemUrl" to ""
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
    fun updateLojaSocialDetails(uid: String, nome: String, descricao: String, imageURL: String) {
        val updatedData = mapOf(
            "nome" to nome,
            "descricao" to descricao,
            "imagemUrl" to imageURL
        )

        firestore.collection("lojaSocial").document(uid).update(updatedData)
            .addOnSuccessListener {
                _firestoreState.value = FirestoreState.Success
            }
            .addOnFailureListener { e ->
                _firestoreState.value = FirestoreState.Error("Erro ao atualizar dados da Loja Social: ${e.message}")
            }
    }



    fun getLojaSocialDetails(uid: String) {
        firestore.collection("lojaSocial").document(uid).get()
            .addOnSuccessListener { document ->
                if (document.exists()) {
                    val nome = document.getString("nome") ?: ""
                    val descricao = document.getString("descricao") ?: ""
                    val imagemUrl = document.getString("imagemUrl")
                    _lojaSocialData.value = LojaSocialData(nome, descricao, imagemUrl)
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
