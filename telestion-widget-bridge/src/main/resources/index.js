var eb = new EventBus('http://localhost:8080/bridge');

eb.onopen = () => {
    console.log('Eventbus opened!');

  eb.registerHandler('org.telestion.core.verticle.RandomPositionPublisher/out#MockPos', (error, message) => {
      console.log('Message from server: ' + JSON.stringify(message));
      document.getElementById('position').innerHTML = "Current position: " + JSON.stringify(message.body);
  });
}