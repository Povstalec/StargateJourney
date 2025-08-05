package net.povstalec.sgjourney.common.compatibility.cctweaked.methods;

public class CallForwardingMethods
{
	/*public static class SetCFDStatus implements InterfaceMethod<AbstractStargateEntity>
	{

		@Override
		public String getName() {
			return "callForward";
		}

		@Override
		public MethodResult use(IComputerAccess computer, ILuaContext context, AbstractInterfaceEntity interfaceEntity, AbstractStargateEntity blockEntity, IArguments arguments) throws LuaException {
			boolean input = arguments.getBoolean(0);
			return context.executeMainThreadTask(() -> {

				blockEntity.setCFD(input);
				return new Object[]{"Call Forwarding successfully"};
			});
		}
	}

	public static class SetCFDTarget implements InterfaceMethod<AbstractStargateEntity>
	{

		@Override
		public String getName() {
			return "setCallForwardingTarget";
		}

		@SuppressWarnings("unchecked")
		@Override
		public MethodResult use(IComputerAccess computer, ILuaContext context, AbstractInterfaceEntity interfaceEntity, AbstractStargateEntity stargate, IArguments arguments) throws LuaException {

			Map<Double, Double> chevronConfiguration = (Map<Double, Double>) arguments.getTable(0);
			MethodResult result = context.executeMainThreadTask(() -> {


				int[] configurationArray = ArrayHelper.tableToArray(chevronConfiguration);


				if(configurationArray.length == 0)
					stargate.setCFDTarget(new Address());
				else if(configurationArray.length < 8)
					throw new LuaException("Array is too short (required length: 8)");
				else if(configurationArray.length > 8)
					throw new LuaException("Array is too long (required length: 8)");
				else if(!ArrayHelper.differentNumbers(configurationArray))
					throw new LuaException("Array contains duplicate numbers");
				else if(!ArrayHelper.isArrayInBounds(configurationArray, 1, 38))
					throw new LuaException("Array contains numbers which are out of bounds <1,38>");

				stargate.setCFDTarget(new Address().fromArray(configurationArray));

				return new Object[] {"Call Forwarding target set successfully"};
			});

			return result;
		}
	}

	public static class GetCFDStatus implements InterfaceMethod<AbstractStargateEntity>
	{

		@Override
		public String getName() {
			return "shouldCallForward";
		}

		@Override
		public MethodResult use(IComputerAccess computer, ILuaContext context, AbstractInterfaceEntity interfaceEntity, AbstractStargateEntity blockEntity, IArguments arguments) throws LuaException {
			return context.executeMainThreadTask(() -> new Object[]{blockEntity.getCFD()});
		}
	}

	public static class GetCFDTarget implements InterfaceMethod<AbstractStargateEntity>
	{

		@Override
		public String getName() {
			return "getCallForwardingTarget";
		}

		@Override
		public MethodResult use(IComputerAccess computer, ILuaContext context, AbstractInterfaceEntity interfaceEntity, AbstractStargateEntity blockEntity, IArguments arguments) throws LuaException {
			return context.executeMainThreadTask(() -> {
				List<Integer> address = Arrays.stream(blockEntity.getCFDTarget().toArray()).boxed().toList();
				return new Object[] {address};
			});
		}
	}*/
}
