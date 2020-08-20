var eb = new EventBus('http://localhost:8080/bridge');

eb.onopen = () => {
    console.log('Eventbus opened!');

    eb.registerHandler('out.connected', (error, message) => {
        console.log('Connection: ' + message);
    });

    eb.registerHandler('out', message => {
        console.log('Message from server: ' + message);
    });

    eb.publish('in', "Hello from Browser");

    eb.send('in', { from: 'client', message: 'Hello' });
}