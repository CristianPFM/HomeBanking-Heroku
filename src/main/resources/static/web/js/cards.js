var app = new Vue({
    el:"#app",
    data:{
        clientInfo: {},
        creditCards: [],
        debitCards: [],
        errorToats: null,
        errorMsg: null,
        firstName: "",
        lastName: "",
        cardId: 0,
    },
    methods:{
        getData: function(){
            axios.get("/api/clients/current")
                .then((response) => {
                    //get client ifo
                    this.clientInfo = response.data;
                    this.creditCards = this.clientInfo.cards.filter(card => card.type == "CREDIT");
                    this.debitCards = this.clientInfo.cards.filter(card => card.type == "DEBIT");
                })
                .catch((error) => {
                    this.errorMsg = "Error getting data";
                    this.errorToats.show();
                })
        },
        formatDate: function(date){
            return new Date(date).toLocaleDateString('en-gb');
        },
        signOut: function(){
            axios.post('/api/logout')
                .then(response => window.location.href="/web/ecobank/index.html")
                .catch(() =>{
                    this.errorMsg = "Sign out failed"
                    this.errorToats.show();
                })
        },
        eliminarTarjeta: function(cardId){
            axios.delete('/api/client/deleteCard/' + cardId)
                .then(response => {
                    this.getData();
                })
                .catch(error => {
                    console.log(error);
                    this.errorMsg = "Error al eliminar tarjeta seleccionada"
                    this.errorToats.show();
                })
        }
    },
    mounted: function(){
        this.errorToats = new bootstrap.Toast(document.getElementById('danger-toast'));
        this.getData();
        this.firstName = JSON.parse(sessionStorage.getItem("firstName"));
        this.lastName = JSON.parse(sessionStorage.getItem("lastName"));
    }
})