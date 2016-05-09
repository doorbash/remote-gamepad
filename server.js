var io = require('socket.io')();
var spawn = require('child_process').spawn

var NUM_CONTROLLERS = 2;

childs = []

for(i=1;i<=NUM_CONTROLLERS;i++)
{
	child = spawn('.\\vJoy',[i]);
	child.stdin.setEncoding('utf-8');
	child.stdout.pipe(process.stdout);
	childs.push(child)
}

io.on('connection', function(socket)
{
	console.log('client connected')
	socket.on('e', function(data)
	{
		data = data.split(" ")
		bin = parseInt(data[1]).toString(2)
		bin = "0".repeat(22-bin.length) + bin
		console.log(bin)
		
		index = parseInt(data[0])
		
		childs[index].stdin.write(data[1] + '\n');
		
	});
	socket.on('disconnect', function() {
		console.log('disconnected')
	});
});
io.listen(3000);
