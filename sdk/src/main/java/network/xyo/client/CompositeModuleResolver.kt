package network.xyo.client

import network.xyo.client.module.AnyModule
import network.xyo.client.module.Module
import network.xyo.client.module.ModuleConfig
import network.xyo.client.module.ModuleFilter
import network.xyo.client.module.ModuleParams
import network.xyo.client.module.ModuleResolver

class CompositeModuleResolver() : ModuleResolver {
    private val resolvers = mutableListOf<ModuleResolver>()
    private val localResolver = SimpleModuleResolver()

    init {
        this.addResolver(this.localResolver)
    }

    fun add(module: AnyModule): ModuleResolver {
        this.localResolver.add(module)
        return this
    }

    override fun addResolver(resolver: ModuleResolver): ModuleResolver {
        this.resolvers.add(resolver)
        return this
    }

     fun remove(addressOrName: String): ModuleResolver {
        this.localResolver.remove(addressOrName)
        return this
    }

    override fun removeResolver(resolver: ModuleResolver): ModuleResolver {
        this.resolvers.remove(resolver)
        return this
    }

    override suspend fun resolve(filter: ModuleFilter?): Set<AnyModule> {
        val resolutions = mutableMapOf<String, AnyModule>()
        this.resolvers.forEach { resolver -> resolver.resolve(filter).forEach { module -> resolutions[module.address] = module } }
        return resolutions.values.toSet()
    }
}