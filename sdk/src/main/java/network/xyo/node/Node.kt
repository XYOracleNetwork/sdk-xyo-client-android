package network.xyo.node

import network.xyo.resolver.CompositeModuleResolver
import network.xyo.module.AbstractModule
import network.xyo.module.AnyModule
import network.xyo.module.ModuleConfig
import network.xyo.module.ModuleFilter
import network.xyo.module.ModuleParams
import network.xyo.payload.IPayload
import network.xyo.payload.JSONPayload
import java.security.InvalidParameterException

const val AddressSchema = "network.xyo.address"

open class Node<TConfig: ModuleConfig, TParams : ModuleParams<TConfig>>(params: TParams) : AbstractModule<TConfig, TParams>(params) {

    override var queries = setOf(
        NodeAttachQuerySchema,
        NodeDetachQuerySchema,
        NodeAttachedQuerySchema,
        NodeRegisteredQuerySchema,
        *super.queries.toTypedArray())

    protected val privateResolver = CompositeModuleResolver()

    protected val registeredModuleMap = mutableMapOf<String, AnyModule>()

    suspend fun attached(): Set<String> {
        return this.attachedModules().map {module -> module.address}.toSet()
    }

    open suspend fun attachedModules(): Set<AnyModule> {
        return this.privateResolver.resolve()
    }

    override suspend fun discover(): Set<IPayload> {
        val childMods = this.attachedModules()
        val childModAddresses = childMods.map { mod ->
            JSONPayload(AddressSchema, mapOf(Pair("address", mod.address), Pair("name", mod.config.name)))
        }

        return setOf(*super.discover().toTypedArray(), *childModAddresses.toTypedArray())
    }

    open suspend fun attach(nameOrAddress: String, external: Boolean = false) {
        return this.attachUsingAddress(nameOrAddress, external) ?: this.attachUsingName(nameOrAddress, external)
    }

    open suspend fun detach(nameOrAddress: String) {
        return this.detachUsingAddress(nameOrAddress) ?: this.detachUsingName(nameOrAddress)
    }

    open fun register(module: AnyModule): Node<TConfig, TParams> {
        if (this.registeredModuleMap[module.address] != null) {
            throw InvalidParameterException("Module already registered at that address[${module.address}]")
        }
        this.registeredModuleMap[module.address] = module
        return this
    }

    open fun registered(): Set<String> {
        return this.registeredModuleMap.keys.toSet()
    }

    open fun registeredModules(): Set<AnyModule> {
        return this.registeredModuleMap.values.toSet()
    }

    open suspend fun unregister(module: AnyModule): Node<TConfig, TParams> {
        this.detach(module.address)
        this.registeredModuleMap.remove(module.address)
        return this
    }

    private suspend fun attachUsingName(name: String, external: Boolean = false) {
        val address = this.moduleAddressFromName(name)
        if (address != null) {
            return this.attachUsingAddress(address, external)
        }
    }

    private suspend fun attachUsingAddress(address: String, external: Boolean = false) {
        val existingModule = this.resolve(ModuleFilter(setOf(address))).elementAtOrNull(0)
        if (existingModule != null) {
            throw InvalidParameterException("Module [${existingModule.config.name ?: existingModule.address}] already attached at address [${address}]")
        }

        val module = this.registeredModuleMap[address]
            ?: throw InvalidParameterException("No module registered at address [${address}]")

        this.privateResolver.addResolver(module.downResolver)

        //give it inside access
        module.upResolver.addResolver(this.privateResolver)

        //give it outside access
        module.upResolver.addResolver(this.upResolver)

        if (external) {
            //expose it externally
            this.downResolver.addResolver(module.downResolver)
        }
    }

    private fun detachUsingAddress(address: String) {
        val module = this.registeredModuleMap[address] ?: return

        this.privateResolver.removeResolver(module.downResolver)

        //remove inside access
        module.upResolver.removeResolver(this.privateResolver)

        //remove outside access
        module.upResolver.removeResolver(this.upResolver)

        //remove external exposure
        this.downResolver.removeResolver(module.downResolver)
    }

    private fun detachUsingName(name: String) {
        val address = this.moduleAddressFromName(name)
        if (address != null) {
            return this.detachUsingAddress(address)
        }
        return
    }

    private fun moduleAddressFromName(name: String): String? {
        val module = this.registeredModuleMap.values.find { value ->
            value.config.name == name
        }
        return module?.address
    }

    companion object {
        const val NodeAttachQuerySchema = "network.xyo.query.node.attach"
        const val NodeDetachQuerySchema = "network.xyo.query.node.detach"
        const val NodeAttachedQuerySchema = "network.xyo.query.node.attached"
        const val NodeRegisteredQuerySchema = "network.xyo.query.node.registered"
    }
}