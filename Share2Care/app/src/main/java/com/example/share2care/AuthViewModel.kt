package com.example.share2care

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.navigation.NavController
import com.google.firebase.auth.FirebaseAuth

class AuthViewModel : ViewModel() {

    private val auth: FirebaseAuth = FirebaseAuth.getInstance()

    private val _authState = MutableLiveData<AuthState>()
    val authState: LiveData<AuthState> = _authState

    init {
        checkAuthStatus()
    }

    fun checkAuthStatus() {
        if (auth.currentUser == null) {
            _authState.value = AuthState.Unauthenticated
        } else {
            _authState.value = AuthState.Authenticated
        }
    }

    fun login(email: String, password: String) {
        if (email.isEmpty() || password.isEmpty()) {
            _authState.value = AuthState.Error("Email ou Password Inválida")
            return
        }

        _authState.value = AuthState.Loading
        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val user = auth.currentUser
                    if (user != null && user.isEmailVerified) {
                        _authState.value = AuthState.Authenticated
                    } else {
                        _authState.value = AuthState.Error("Por favor, verifique seu e-mail antes de fazer login.")
                        auth.signOut() // Desloga o usuário não verificado
                    }
                } else {
                    _authState.value = AuthState.Error(task.exception?.message ?: "Erro ao fazer login")
                }
            }
    }

    fun loginAsAnonymous() {
        _authState.value = AuthState.Loading
        auth.signInAnonymously()
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    _authState.value = AuthState.Authenticated
                } else {
                    _authState.value = AuthState.Error(task.exception?.message ?: "Erro ao entrar anonimamente")
                }
            }
    }

    fun register(email: String, password: String, firestoreViewModel: FirestoreViewModel, navController: NavController) {
        if (email.isEmpty() || password.isEmpty()) {
            _authState.value = AuthState.Error("Email ou Password Inválida")
            return
        }

        _authState.value = AuthState.Loading
        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val user = auth.currentUser
                    user?.sendEmailVerification()
                        ?.addOnCompleteListener { emailTask ->
                            if (emailTask.isSuccessful) {
                                _authState.value = AuthState.Error("Verifique seu e-mail antes de continuar.")
                                navController.navigate("initial")
                            } else {
                                _authState.value = AuthState.Error(emailTask.exception?.message ?: "Erro ao enviar e-mail de verificação.")
                            }
                        }

                    // Opcional: Salvar os dados no Firestore apenas após a verificação do e-mail
                    val uid = user?.uid
                    if (uid != null) {
                        firestoreViewModel.saveLojaSocialToFirestore(uid)
                    }
                } else {
                    _authState.value = AuthState.Error(task.exception?.message ?: "Erro ao registrar usuário")
                }
            }
    }

    fun sendPasswordResetEmail(email: String, onSuccess: () -> Unit, onFailure: (String) -> Unit) {
        if (email.isEmpty()) {
            onFailure("O e-mail não pode estar vazio")
            return
        }

        auth.sendPasswordResetEmail(email)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    onSuccess()
                } else {
                    onFailure(task.exception?.message ?: "Erro ao enviar o e-mail de redefinição de senha")
                }
            }
    }

    fun signout() {
        auth.signOut()
        _authState.value = AuthState.Unauthenticated
    }

}

sealed class AuthState {
    object Authenticated : AuthState()
    object Unauthenticated : AuthState()
    object Loading : AuthState()
    data class Error(val message: String) : AuthState()
}
