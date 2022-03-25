var app = new Vue({
    el:"#app",
    data:{
        accountInfo: {},
        transactions: [],
        errorToats: null,
        errorMsg: null,
        firstName: "",
        lastName: ""
    },
    methods:{
        getData: function(){
            const urlParams = new URLSearchParams(window.location.search);
            const id = urlParams.get('id');
            axios.get(`/api/accounts/${id}`)
                .then((response) => {
                    //get client ifo
                    this.accountInfo = response.data;
                    this.transactions = this.accountInfo.transactions;
                    console.log("aaaa", this.accountInfo.number);
                    this.accountInfo.transactions.sort((a,b) => parseInt(b.id - a.id))
                })
                .catch((error) => {
                    // handle error
                    this.errorMsg = "Error getting data";
                    this.errorToats.show();
                })
        },
        formatDate: function(date){
            return new Date(date).toLocaleDateString('en-gb');
        },
        //metodo para descargar el pdf del historial de transacciones por cuenta
        downloadPdf: function(){
            let postConfig = {
                headers: {
                    'content-type': 'application/x-www-form-urlencoded'
                },
                responseType: 'blob',
            }
            axios.get(`/api/transactions/export/pdf/${this.accountInfo.number}`, postConfig)
                .then(response => {
                    console.log(this.accountInfo.number);
                    var fileURL = window.URL.createObjectURL(new Blob([response.data]));
                    var fileLink = document.createElement('a');

                    fileLink.href = fileURL;
                    fileLink.setAttribute('download', 'historial_transacciones.pdf');
                    document.body.appendChild(fileLink);

                    fileLink.click();
                })
                .catch((error) =>{
                    console.log(error);
                    this.errorMsg = "Error al descargar archivo PDF"
                    this.errorToats.show();
                })
        },
        signOut: function(){
            axios.post('/api/logout')
                .then(response => window.location.href="/web/ecobank/index.html")
                .catch(() =>{
                    this.errorMsg = "Sign out failed"
                    this.errorToats.show();
                })
        },
    },
    mounted: function(){
        this.errorToats = new bootstrap.Toast(document.getElementById('danger-toast'));
        this.getData();
        this.firstName = JSON.parse(sessionStorage.getItem("firstName"));
        this.lastName = JSON.parse(sessionStorage.getItem("lastName"));
    }
})
