var app = new Vue({
    el:"#app",
    data:{
        clientInfo: {},
        error: null,
        divisas: {}
    },
    methods:{
        getData: function(){
            axios.get("/api/clients/current")
            .then(function (response) {
                //get client ifo
                app.clientInfo = response.data;
            })
            .catch(function (error) {
                // handle error
                app.error = error;
            })
        },
        formatDate: function(date){
            return new Date(date).toLocaleDateString('en-gb');
        },
    },
    mounted: function(){
        this.getData();
    },
    created() {
        this.getDivisas();
    }
})