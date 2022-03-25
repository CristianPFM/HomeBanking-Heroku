var app = new Vue({
    el: "#app",
    data: {
        email: "",
        password: "",
        firstName: "",
        lastName: "",
        errorToats: null,
        errorMsg: "",
        showSignUp: false,
        valorDolar: null,
        valaorEuro: null,
        valorUf: null,
        userRegisterEmail: "",
        userRegisterPassword: "",
        nameRecover: "",
        emailRecover: "",
        nameRecover2: "",
        emailRecover2: "",
        temporalPassword: "",
        newPassword: "",
        fullNameContact: "",
        subjectContact: "",
        emailContact: "",
        textContact: "",
        contactoEnviado: false,
        cargando: false
    },
    methods: {
        signIn: function (event) {
            event.preventDefault();
            let config = {
                headers: {
                    'content-type': 'application/x-www-form-urlencoded'
                }
            }
            axios.post('/api/login', `email=${this.email}&password=${this.password}`, config)
                .then(response => window.location.href = "/web/ecobank/accounts.html")
                .catch(() => {
                    console.log("Error");
                    this.errorMsg = "Ocurrió un error al iniciar sesión, por favor verifica los datos ingresados";
                    this.errorToats.show();
                })
        },
        signUp: function (event) {
            event.preventDefault();
            let config = {
                headers: {
                    'content-type': 'application/x-www-form-urlencoded'
                }
            }
            axios.post('/api/clients', `firstName=${this.firstName}&lastName=${this.lastName}&email=${this.userRegisterEmail}&password=${this.userRegisterPassword}`, config)
                .then(() => {
                    this.email = this.userRegisterEmail;
                    this.password = this.userRegisterPassword;
                    this.signIn(event)
                })
                .then(() => { this.registerModal.close() })
                .catch(() => {
                    this.errorMsg = "Error al registrarse, revise los datos"
                    this.errorToats.show();
                })
        },
        showSignUpToogle: function () {
            this.showSignUp = !this.showSignUp;
        },
        formatDate: function (date) {
            return new Date(date).toLocaleDateString('en-gb');
        },
        getDivisas: async function () {
            await axios.get('https://mindicador.cl/api')
                .then(response => {
                    //this.valorDolar = response.data.dolar.valor;
                    
                    //guardar variable en sessionStorage
                    sessionStorage.setItem("valorDolar", JSON.stringify((response.data.dolar.valor).toString().replace(".", ",")));
                    sessionStorage.setItem("valorEuro", JSON.stringify((response.data.euro.valor).toString().replace(".", ",")));
                    sessionStorage.setItem("valorUf", JSON.stringify((response.data.uf.valor).toString().replace(".", ",")));

                    //obtener dato desde sessionStorage
                    this.valorDolar = JSON.parse(sessionStorage.getItem("valorDolar"));
                    this.valorEuro = JSON.parse(sessionStorage.getItem("valorEuro"));
                    this.valorUf = JSON.parse(sessionStorage.getItem("valorUf"));
                })
                .catch(error => {
                    console.log(error);
                    this.errorMsg = "Error al obtener API divisas"
                    this.errorToats.show();
                })
        },
        registerUser: function () {
            this.registerModal.show();
        },
        passwordRecover: function () {
            this.passwordRecoverModal.show();
        },
        recoverPasswordOtp: function (event) {
            this.cargando = true;
            let config = {
                headers: {
                    'content-type': 'application/x-www-form-urlencoded'
                }
            }
            axios.post('/api/resetPassword', `name=${this.nameRecover.toLowerCase()}&email=${this.emailRecover.toLowerCase()}`, config)
                .then(response => {
                    console.log(response);
                    this.cargando = false;
                    this.passwordRecoverModal.hide();
                    this.passwordRecoverModalOtpConfirmed.show();
                })
                .catch(error => {
                    console.log(error);
                    this.errorMsg = "Error al recuperar contraseña, revise los datos ingresados"
                    this.errorToats.show();
                })
        },
        changePassword: function (event) {
            let config = {
                headers: {
                    'content-type': 'application/x-www-form-urlencoded'
                }
            }
            axios.put('/api/changePassword', `name=${this.nameRecover2.toLowerCase()}&email=${this.emailRecover2.toLowerCase()}&temporalPass=${this.temporalPassword}&newPass=${this.newPassword}`, config)
                .then(response => {
                    console.log(response);
                    this.passwordRecoverModalOtpConfirmed.hide();
                })
                .catch(error => {
                    console.log(error);
                    this.errorMsg = "Error al cambiar la contraseña, revise los datos ingresados"
                    this.errorToats.show();
                })
        },
        contacto: function () {
            let config = {
                headers: {
                    'content-type': 'application/x-www-form-urlencoded'
                }
            }
            axios.post('/api/contact', `fullName=${this.fullNameContact}&email=${this.emailContact}&subject=${this.subjectContact}&text=${this.textContact}`, config)
                .then(response => {
                    console.log(response);
                    this.contactoEnviado = true;
                })
                .catch(error => {
                    console.log(error);
                    this.errorMsg = "Error al recuperar contraseña, revise los datos ingresados"
                    this.errorToats.show();
                })
        },
        scrollToElement(options, className) {
            const el = this.$el.getElementsByClassName(className)[0];

            if (el) {
                el.scrollIntoView(options);
            }
        }
    },
    mounted: async function () {
        this.errorToats = new bootstrap.Toast(document.getElementById('danger-toast'));
        this.registerModal = new bootstrap.Modal(document.getElementById('registerModal'));
        this.passwordRecoverModal = new bootstrap.Modal(document.getElementById('passwordRecoverModal'));
        this.passwordRecoverModalOtpConfirmed = new bootstrap.Modal(document.getElementById('passwordRecoverModalOtpConfirmed'));

        if (
            !JSON.parse(sessionStorage.getItem("valorDolar")) ||
            !JSON.parse(sessionStorage.getItem("valorEuro")) ||
            !JSON.parse(sessionStorage.getItem("valorUf"))) {

            await this.getDivisas();
            //console.log("dentro de if: " + this.valorDolar);
        } else {
            this.valorDolar = JSON.parse(sessionStorage.getItem("valorDolar"));
            this.valorEuro = JSON.parse(sessionStorage.getItem("valorEuro"));
            this.valorUf = JSON.parse(sessionStorage.getItem("valorUf"));
            //console.log("fuera de if: " + this.valorDolar);
        }

    }
})