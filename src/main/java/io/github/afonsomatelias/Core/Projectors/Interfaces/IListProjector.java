package io.github.afonsomatelias.Core.Projectors.Interfaces;

import java.util.List;

import io.github.afonsomatelias.Callback.ICallbacks.I1Action;
import io.github.afonsomatelias.Options.Interfaces.IMappingListActions;

public interface IListProjector<S> {
	
	public <D> List<D> to(Class<D> clazz);
	
	public <D> List<D> to(Class<D> clazz, I1Action<IMappingListActions<S, D>> modifier);
}
