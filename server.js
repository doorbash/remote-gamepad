var io = require('socket.io')();
var spawn = require('child_process').spawn, child = spawn('.\\vJoy');

child.stdin.setEncoding('utf-8');
child.stdout.pipe(process.stdout);

io.on('connection', function(socket)
{
	console.log('client connected')
	socket.on('e', function(data)
	{
		bin = parseInt(data).toString(2)
		bin = "0".repeat(22-bin.length) + bin
		console.log(bin)
		
		child.stdin.write(data + '\n');
		
	});
	socket.on('disconnect', function() {
		console.log('disconnected')
	});
});
io.listen(3000);