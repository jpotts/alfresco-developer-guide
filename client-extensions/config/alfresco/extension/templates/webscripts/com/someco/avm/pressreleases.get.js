script : {
	// store and path provided as args   
	if (args.s == null || args.s.length == 0) {
		status.code = 400;
		status.message = "Store is a required argument.";
		status.redirect = true;
		break script;
	}
	
	if (args.p == null || args.p.length == 0) {
		status.code = 400;
		status.message = "Path is a required argument.";
		status.redirect = true;
		break script;
	}		
    
	// get avm node
	var store = avm.lookupStore(args.s);
	if (store == null || store == undefined) {
		status.code = 404;
		status.message = "Store " + args.s + " not found.";
		status.redirect = true;
		break script;
	}
	
	// get press release data folder
	var pressReleaseNode = avm.lookupNode(args.s + ":" + args.p);
	if (pressReleaseNode == undefined) {
		status.code = 404;
		status.message = "Could not find press release folder. Path:" + args.p + " Store:" + args.s;
		status.redirect = true;
		break script;
	}
    
	// set store and folder in the model
	model.store = store;
	model.folder = pressReleaseNode;
}