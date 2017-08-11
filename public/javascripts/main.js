$(function() {
   const sock = new WebSocket("ws://localhost:9000/stream");

   var app = new Vue({
      el: "#app",
      data: {
         status: "Waiting...",
         state: 'loading',
         nominations: [],
         results: []
      },
      methods: {
         submitNomination: function() {
            var $nom = $('#nom');
            sock.send(JSON.stringify({action: "nominate", name: $nom.val()}));
            $nom.val('');
         },
         submitBallot: function() {
            var i;
            var votes = [];
            console.log(this.nominations);
            for (i = 0; i < this.nominations.length; i++) {
               var rank = parseInt($("#nom-" + i).val(), 10);
               if (rank) {
                  votes.push({id: this.nominations[i], rank: rank});
               }
            }
            console.log(votes);
            sock.send(JSON.stringify({action: "submitBallot", votes: votes}))
         }
      }
   });

   sock.addEventListener('open', function(event) {
      //sock.send('{"name": "Del Taco"}');
      //console.log('sending');
   });
   sock.addEventListener('message', function(event) {
      app.status = event.data;

      var data = JSON.parse(event.data);
      if (data.state) {
         if (data.state === "nominate") {
            app.nominations = data.noms;
         }
         else if (data.state === "vote") {
            app.nominations = data.noms;
         }
         app.state = data.state;
      }
      else if (data.event) {
         if (data.event === "nominated") {
            app.nominations.push(data.name);
         }
         else if (data.event === "results") {
            app.results = data.results;
         }
      }
   });
});