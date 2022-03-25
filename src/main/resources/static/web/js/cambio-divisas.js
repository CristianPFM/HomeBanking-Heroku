var app = new Vue({
    el: "#app",
    data: {
        valorDolar: null,
        valorEuro: null,
        valorUf: null,
        firstName: "",
        lastName: "",
        montoParaConvertir: null,
        montoConvertido: 0,
        montoConvertidoEuros: 0,
        readonly: true,
        options: [
            'foo',
            'bar',
            'baz'
        ]
    },
    methods: {
        formatDate: function (date) {
            return new Date(date).toLocaleDateString('en-gb');
        },
        signOut: function () {
            axios.post('/api/logout')
                .then(response => window.location.href = "/web/ecobank/index.html")
                .catch(() => {
                    this.errorMsg = "Sign out failed"
                    this.errorToats.show();
                })
        },
        convertirDivisa: function () {
            this.montoConvertido = (montoParaConvertir.value / (this.valorDolar).replace(",", "."));
            this.montoConvertidoEuros = (montoParaConvertir.value / (this.valorEuro).replace(",", "."));
        }
    },
    created: function () {
        this.valorDolar = JSON.parse(sessionStorage.getItem("valorDolar"))
        this.valorEuro = JSON.parse(sessionStorage.getItem("valorEuro"));
        this.valorUf = JSON.parse(sessionStorage.getItem("valorUf"));
        this.errorToats = new bootstrap.Toast(document.getElementById('danger-toast'));
        this.firstName = JSON.parse(sessionStorage.getItem("firstName"));
        this.lastName = JSON.parse(sessionStorage.getItem("lastName"));
        this.valorDolar = JSON.parse(sessionStorage.getItem("valorDolar"));
    }
})