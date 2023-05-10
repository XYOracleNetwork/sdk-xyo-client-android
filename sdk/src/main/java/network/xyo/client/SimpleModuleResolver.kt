package network.xyo.client

import network.xyo.client.module.AnyModule
import network.xyo.client.module.Module
import network.xyo.client.module.ModuleConfig
import network.xyo.client.module.ModuleFilter
import network.xyo.client.module.ModuleParams
import network.xyo.client.module.ModuleResolver

class SimpleModuleResolver : ModuleResolver {
    private val addressToName = mutableMapOf<String, String>()
    private val modules = mutableMapOf<String, Module<ModuleConfig, ModuleParams<ModuleConfig>>>()

    fun add(module: Module<ModuleConfig, ModuleParams<ModuleConfig>>): ModuleResolver {
        this.modules[module.address] = module
        return this
    }

    override fun addResolver(resolver: ModuleResolver): ModuleResolver {
        throw NotImplementedError()
    }

    fun remove(address: String): ModuleResolver {
        if (this.modules.containsKey(address)) {
            this.modules.remove(address)
            val name = this.addressToName[address]
            if (name != null) {
                this.addressToName.remove(address)
            }
        }
        return this
    }

    override fun removeResolver(resolver: ModuleResolver): ModuleResolver {
        throw NotImplementedError()
    }

    override suspend fun resolve(filter: ModuleFilter?): Set<AnyModule> {
        val filteredByName = this.resolveByName(this.modules.values.toSet(), filter?.name)

        return this.resolveByAddress(filteredByName, filter?.name)
    }

    private fun resolveByAddress(modules: Set<AnyModule>, addresses: Set<String>?): Set<AnyModule> {
        return if (addresses == null) {
            modules
        } else {
            modules.filter { module -> addresses.contains(module.address) }.toSet()
        }
    }

    private fun resolveByName(modules: Set<AnyModule>, names: Set<String>?): Set<AnyModule> {
        return if (names == null) {
            modules
        } else {
            modules.filter { module -> names.contains(module.config.name) }.toSet()
        }
    }
}
