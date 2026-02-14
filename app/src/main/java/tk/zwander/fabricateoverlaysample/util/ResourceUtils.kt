package tk.zwander.fabricateoverlaysample.util

import android.annotation.SuppressLint
import android.content.Context
import android.content.res.Resources.NotFoundException
import android.util.TypedValue
import com.reandroid.apk.ApkModule
import com.reandroid.arsc.model.ResourceEntry
import tk.zwander.fabricateoverlaysample.data.AvailableResourceItemData
import java.util.TreeMap

val supportedTypes = setOf(
    "string",
    "color",
    "dimen",
    "fraction",
    "integer",
    "bool",
    "id"
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
                        "fraction" -> TypedValue.TYPE_STRING // TYPE_FRACTION
                        "string" -> TypedValue.TYPE_STRING
                        "id" -> TypedValue.TYPE_INT_DEC
                        else -> 0
                    }

                    val current = try {
                        context.getCurrentResourceValue(packageName, fqrn)
                    } catch (e: Throwable) {
                        e.printStackTrace()
                        arrayOf()
                    }

                    list[rType]!!
                        .add(
                            AvailableResourceItemData(
                                fqrn,
                                rName,
                                valueType,
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
fun Context.getCurrentResourceValue(packageName: String, fqrn: String): Array<String> {
    val res = packageManager.getResourcesForApplication(packageName)

    try {
        val value = TypedValue()
        res.getValue(fqrn, value, false /* resolveRefs */)
        val valueString = value.coerceToString()
        res.getValue(fqrn, value, true /* resolveRefs */)
        val resolvedString = value.coerceToString()

        return arrayOf(
            if (valueString == resolvedString) {
                resolvedString.toString()
            } else {
                "$valueString -> $resolvedString"
            }
        )
    } catch (e: NotFoundException) {
        e.printStackTrace()
    }

    return try {
        val split = fqrn.split(":", "/")

        val pkg = split[0]
        val type = split[1]
        val name = split[2]
        val resid = res.getIdentifier(name, type, pkg)
        if (resid == 0) {
            throw NotFoundException()
        }
        val array = res.obtainTypedArray(resid)
        val tv = TypedValue()

        val items = ArrayList<String>(array.length())

        for (i in 0 until array.length()) {
            array.getValue(i, tv)
            items.add(tv.coerceToString().toString())
        }
        array.recycle()

        items.toTypedArray()
    } catch (e: NotFoundException) {
        e.printStackTrace()
        arrayOf()
    }
}