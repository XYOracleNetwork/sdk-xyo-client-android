package network.xyo.client

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

    fun add(module: Module<ModuleConfig, ModuleParams<ModuleConfig>>): ModuleResolver {
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

    override fun resolve(filter: ModuleFilter?): List<Module<ModuleConfig, ModuleParams<ModuleConfig>>> {
        val resolutions = mutableMapOf<String, Module<ModuleConfig, ModuleParams<ModuleConfig>>>()
        this.resolvers.forEach { resolver -> resolver.resolve(filter).forEach { module -> resolutions.set(module.address, module) } }
        return resolutions.values.toList()
    }
}