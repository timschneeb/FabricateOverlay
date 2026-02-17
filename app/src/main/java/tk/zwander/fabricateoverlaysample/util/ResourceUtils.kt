package tk.zwander.fabricateoverlaysample.util

import android.annotation.SuppressLint
import android.content.Context
import android.content.res.Resources.NotFoundException
import android.util.TypedValue
import com.reandroid.apk.ApkModule
import com.reandroid.arsc.model.ResourceEntry
import timber.log.Timber
import tk.zwander.fabricateoverlaysample.data.AvailableResourceItemData
import tk.zwander.fabricateoverlaysample.data.ResourceValueInfo
import java.util.TreeMap

val supportedTypes = setOf(
    "string",
    "color",
    "dimen",
    "fraction",
    "integer",
    "bool"
)

fun getAppResources(
    context: Context,
    packageName: String
): Map<String, List<AvailableResourceItemData>> {

    val module: ApkModule = ApkParser(context, packageName).module
    if (!module.hasTableBlock()){
        return emptyMap()
    }

    val list = TreeMap<String, MutableList<AvailableResourceItemData>>()

    module.tableBlock.listPackages().forEach { packageBlock ->
        // Ensure types are in stable order
        packageBlock.sortTypes()

        packageBlock.listSpecTypePairs().forEach { specTypePair ->
            val typeName = specTypePair.typeName
            if (typeName !in supportedTypes)
                return@forEach

            val iterator: MutableIterator<ResourceEntry?> = specTypePair.getResources()
            while (iterator.hasNext()) {
                val resourceEntry: ResourceEntry? = iterator.next()
                if (resourceEntry != null && !resourceEntry.isEmpty) {
                    // Determine resource type name
                    val rType = resourceEntry.type ?: typeName
                    val rName = resourceEntry.getName()

                    if (rType.isNullOrEmpty() || rName.isEmpty())
                        continue

                    if (list[rType] == null) {
                        list[rType] = ArrayList()
                    }

                    /*
                     * The package block name is not guaranteed to be the same as the application package name.
                     * System apps that have been renamed using the "original-package" manifest attribute will
                     * have a package block name that matches the original package, not the renamed one.
                     */
                    val pkgBlockName = packageBlock.name ?: packageName

                    // Build fully-qualified resource name
                    val fqrn = "${pkgBlockName}:$rType/${rName}"

                    // Infer a simple value type for some common resource types
                    val valueType = when (rType) {
                        "integer" -> TypedValue.TYPE_INT_DEC
                        "color" -> TypedValue.TYPE_INT_COLOR_ARGB8
                        "bool" -> TypedValue.TYPE_INT_BOOLEAN
                        "dimen" -> TypedValue.TYPE_DIMENSION
                        "fraction" -> TypedValue.TYPE_FRACTION
                        "string" -> TypedValue.TYPE_STRING
                        else -> 0
                    }

                    val current = try {
                        context.getCurrentResourceValue(pkgBlockName, fqrn)
                    } catch (e: Throwable) {
                        Timber.e(e, "Failed to get current value for resource $fqrn")
                        arrayOf()
                    }

                    list[rType]!!
                        .add(
                            AvailableResourceItemData(
                                fqrn,
                                rName,
                                current.lastOrNull()?.type ?: valueType.also {
                                    Timber.w("Could not determine actual type for resource $fqrn, defaulting to $it")
                                },
                                current
                            )
                        )
                }
            }
        }
    }

    list.forEach { (_, v) ->
        v.sort()
    }

    return list
}


@SuppressLint("DiscouragedApi")
fun Context.getCurrentResourceValue(packageName: String, fqrn: String): Array<ResourceValueInfo> {
    val res = packageManager.getResourcesForApplication(packageName)

    // Resolve a typed value recursively following TYPE_REFERENCE/TYPE_ATTRIBUTE
    fun resolveChain(initial: TypedValue): List<ResourceValueInfo> {
        val chain = ArrayList<ResourceValueInfo>()
        val visited = mutableSetOf<Int>()
        var current = TypedValue()
        current.setTo(initial)

        while (true) {
            chain.add(
                ResourceValueInfo(
                    current.type,
                    current.data,
                    if(current.type == TypedValue.TYPE_STRING) current.string?.toString() else null
                )
            )

            // If this is a reference to another resource, try to follow it by resourceId
            val isRef = current.type == TypedValue.TYPE_REFERENCE || current.type == TypedValue.TYPE_ATTRIBUTE
            val resid = current.resourceId
            if (isRef && resid != 0 && !visited.contains(resid)) {
                visited.add(resid)
                try {
                    val next = TypedValue()
                    // Get the value without resolving refs so we can track the chain step-by-step
                    res.getValue(resid, next, false)
                    current = next
                    continue
                } catch (e: Exception) {
                    // If we can't resolve by id, stop following
                    Timber.e(e, "Failed to resolve reference with id $resid")
                    break
                }
            }

            // Not a reference (or can't follow): we're at the final value
            break
        }

        return chain
    }

    try {
        val value = TypedValue()
        // Try to get the named value. Do not resolve refs here: we want the original step first
        res.getValue(fqrn, value, false /* resolveRefs */)

        return resolveChain(value).toTypedArray()
    } catch (_: NotFoundException) {
        Timber.e("Resource not found: $fqrn")
        return arrayOf()
    }
}