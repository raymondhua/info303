var customerApi = '//localhost:9000/addAccount';
const app = Vue.createApp({
 
    data() {
    return {
        customer: new Object()
    };
    },
 
    mounted() {

    },
 
    methods: {
    
    addCustomer() {
        axios.post(customerApi, this.customer)
            .then(() => {
                // update the catalogue
            })
            .catch(error => {
                console.error(error);
                alert("An error occurred - check the console for details.");
            });
        }
    }
});
 
// mount the page at the <main> tag - this needs to be the last line in the file
app.mount("main");