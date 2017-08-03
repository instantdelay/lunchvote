$(function() {
   const sock = new WebSocket("ws://localhost:9000/stream");

   var app = new Vue({
      el: "#app",
      data: {
         status: "Waiting...",
         state: 'loading',
         nominations: []
      },
      methods: {
         submit: function() {
            var $nom = $(nom);
            sock.send(JSON.stringify({action: "nominate", name: $nom.val()}));
            $nom.val('');
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
         app.state = data.state;
      }
      else if (data.event) {
         if (data.event === "nominated") {
            app.nominations.push(data.name);
         }
      }
   });
});